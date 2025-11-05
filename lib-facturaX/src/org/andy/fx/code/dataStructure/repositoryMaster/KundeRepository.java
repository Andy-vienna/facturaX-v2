package org.andy.fx.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.main.Einstellungen;

public class KundeRepository {

    public List<Kunde> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Kunde ORDER BY id", Kunde.class).list();
        }
    }
    
    public Kunde findById(String id) {
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery(
                    "FROM Kunde r WHERE r.id = :id", Kunde.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public String findMaxNummer() {
    	String sql = null;
    	switch(Einstellungen.getDbSettings().dbType) {
    	case "mssql" -> sql ="SELECT ISNULL(MAX(TRY_CAST(SUBSTRING(s.id, 1, 10) AS int)), 0) + 1 FROM dbo.tblKunde s";
    	case "postgre" -> sql = "SELECT COALESCE(MAX(SUBSTR(s.id,1,10)::int), 0) + 1 FROM public.tblkunde s";
    	}
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
    		Integer next = ((Number) session.createNativeQuery(sql, Integer.class)
    		    .getSingleResult()).intValue();
    		if (next == 1) {
    			next = 1000001;
    		}
    		return String.format("%03d", next);
    	}
    }

    public void insert(Kunde kunde) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(kunde);
            tx.commit();
        }
    }

    public void update(Kunde kunde) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(kunde);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Kunde kunde = session.find(Kunde.class, id);
            if (kunde != null) session.remove(kunde);
            tx.commit();
        }
    }
}

