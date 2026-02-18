package org.andy.fx.code.dataStructure.repositoryProductive;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AngebotRepository {
	
	public List<Angebot> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM Angebot ORDER BY idNummer", Angebot.class).list();
        }
    }

    public List<Angebot> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Angebot r WHERE r.jahr = :jahr ORDER BY r.idNummer", Angebot.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Angebot findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Angebot r WHERE r.idNummer = :id", Angebot.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public Integer findMaxNummerByJahr(int jahr) {
        String prefix = "AN-" + jahr + "-";
        //int prefixLength = prefix.length();

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
                           .setParameter("prefix", prefix + "%");
            			   //.setParameter("prefixLen", prefixLength);

            Integer maxNummer = q.setMaxResults(1).uniqueResult();
            return maxNummer == null ? 0 : maxNummer;
        }
    }


    public void save(Angebot angebot) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(angebot);
            tx.commit();
        }
    }

    public void update(Angebot angebot) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(angebot);
            tx.commit();
        }
    }
}

