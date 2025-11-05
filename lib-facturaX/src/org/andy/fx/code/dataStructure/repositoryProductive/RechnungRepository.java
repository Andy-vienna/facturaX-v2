package org.andy.fx.code.dataStructure.repositoryProductive;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.main.Einstellungen;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RechnungRepository {
	
	public List<Rechnung> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM Rechnung ORDER BY idNummer", Rechnung.class).list();
        }
    }

    public List<Rechnung> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Rechnung r WHERE r.jahr = :jahr ORDER BY r.idNummer", Rechnung.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Rechnung findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Rechnung r WHERE r.idNummer = :id", Rechnung.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public Integer findMaxNummerByJahr(int jahr) {
        String prefix = "RE-" + jahr + "-";
        int prefixLength = prefix.length();

        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            String dbType = Einstellungen.getDbSettings().dbType;
            if (dbType == null || dbType.isBlank()) {
                dbType = session.doReturningWork(c -> c.getMetaData().getDatabaseProductName());
            }
            boolean isPg = dbType.toLowerCase(java.util.Locale.ROOT).contains("postgre");

            String sql;
            if (isPg) {
            	// PostgreSQL Datenbank
                sql = """
                    SELECT NULLIF(
                             split_part(split_part(r.idnummer, '-', 3), '/', 1),
                             ''
                           )::int AS num
                    FROM public.tblre r
                    WHERE r.jahr = :jahr AND r.idnummer LIKE :prefix
                    ORDER BY num DESC
                    """;
            } else {
            	// MS SQL Datenbank
                sql = """
                    SELECT TRY_CONVERT(int,
                           SUBSTRING(r.idNummer, :prefixLen + 1,
                                    CASE WHEN CHARINDEX('/', r.idNummer) > 0
                                         THEN CHARINDEX('/', r.idNummer) - :prefixLen - 1
                                         ELSE LEN(r.idNummer) - :prefixLen END)
                           ) AS num
                    FROM dbo.tblRe r
                    WHERE r.jahr = :jahr AND r.idNummer LIKE :prefix
                    ORDER BY num DESC
                    """;
            }

            var q = session.createNativeQuery(sql, Integer.class)
                           .setParameter("jahr", jahr)
                           .setParameter("prefix", prefix + "%");
            if (!isPg) q.setParameter("prefixLen", prefixLength);

            Integer maxNummer = q.setMaxResults(1).uniqueResult();
            return maxNummer == null ? 0 : maxNummer;
        }
    }

    public void save(Rechnung rechnung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(rechnung);
            tx.commit();
        }
    }

    public void update(Rechnung rechnung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(rechnung);
            tx.commit();
        }
    }
}

