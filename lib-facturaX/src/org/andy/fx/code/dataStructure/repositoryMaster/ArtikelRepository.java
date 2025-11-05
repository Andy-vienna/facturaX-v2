package org.andy.fx.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityMaster.Artikel;
import org.andy.fx.code.main.Einstellungen;

public class ArtikelRepository {

    public List<Artikel> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Artikel ORDER BY id", Artikel.class).list();
        }
    }
    
    public String findMaxNummer() {
    	String sql = null;
    	switch(Einstellungen.getDbSettings().dbType) {
    	case "mssql" -> sql ="SELECT ISNULL(MAX(TRY_CAST(SUBSTRING(s.id, 1, 10) AS int)), 0) + 1 FROM dbo.tblArtikel s";
    	case "postgre" -> sql = "SELECT COALESCE(MAX(SUBSTR(s.id,1,10)::int), 0) + 1 FROM public.tblartikel s";
    	}
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
    		Integer next = ((Number) session.createNativeQuery(sql, Integer.class)
    		    .getSingleResult()).intValue();
    		if (next == 1) {
    			next = 3000001;
    		}
    		return String.format("%03d", next);
    	}
    }

    public void insert(Artikel artikel) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(artikel);
            tx.commit();
        }
    }

    public void update(Artikel artikel) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(artikel);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Artikel artikel = session.find(Artikel.class, id);
            if (artikel != null) session.remove(artikel);
            tx.commit();
        }
    }
}

