package org.andy.fx.code.dataStructure.repositoryProductive;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BestellungRepository {
	
	public List<Bestellung> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM Bestellung ORDER BY idNummer", Bestellung.class).list();
        }
    }

    public List<Bestellung> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Bestellung r WHERE r.jahr = :jahr ORDER BY r.idNummer", Bestellung.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Bestellung findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Bestellung r WHERE r.idNummer = :id", Bestellung.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
     
    public Integer findMaxNummerByJahr(int jahr) {
        String prefix = "AN-" + jahr + "-";
        int prefixLength = prefix.length();

        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {

            String sql = """
                SELECT NULLIF(
                         split_part(split_part(r.idnummer, '-', 3), '/', 1),
                         ''
                       )::int AS num
                FROM public.tblan r
                WHERE r.jahr = :jahr AND r.idnummer LIKE :prefix
                ORDER BY num DESC
                """;

            var q = session.createNativeQuery(sql, Integer.class)
                           .setParameter("jahr", jahr)
                           .setParameter("prefix", prefix + "%")
            			   .setParameter("prefixLen", prefixLength);

            Integer maxNummer = q.setMaxResults(1).uniqueResult();
            return maxNummer == null ? 0 : maxNummer;
        }
    }

    public void save(Bestellung bestellung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(bestellung);
            tx.commit();
        }
    }

    public void update(Bestellung bestellung) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(bestellung);
            tx.commit();
        }
    }
}

