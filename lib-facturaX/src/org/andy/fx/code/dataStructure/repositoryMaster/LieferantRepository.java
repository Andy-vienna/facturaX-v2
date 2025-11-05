package org.andy.fx.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.main.Einstellungen;

public class LieferantRepository {

	public List<Lieferant> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Lieferant", Lieferant.class).list();
        }
    }
	
	public Lieferant findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery(
                    "FROM Lieferant r WHERE r.id = :id", Lieferant.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public String findMaxNummer() {
    	String sql = null;
    	switch(Einstellungen.getDbSettings().dbType) {
    	case "mssql" -> sql ="SELECT ISNULL(MAX(TRY_CAST(SUBSTRING(s.id, 1, 10) AS int)), 0) + 1 FROM dbo.tblLieferant s";
    	case "postgre" -> sql = "SELECT COALESCE(MAX(SUBSTR(s.id,1,10)::int), 0) + 1 FROM public.tbllieferant s";
    	}
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
    		Integer next = ((Number) session.createNativeQuery(sql, Integer.class)
    		    .getSingleResult()).intValue();
    		if (next == 1) {
    			next = 2000001;
    		}
    		return String.format("%03d", next);
    	}
    }

    public void insert(Lieferant lieferant) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(lieferant);
            tx.commit();
        }
    }

    public void update(Lieferant lieferant) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(lieferant);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Lieferant lieferant = session.find(Lieferant.class, id);
            if (lieferant != null) session.remove(lieferant);
            tx.commit();
        }
    }
}

