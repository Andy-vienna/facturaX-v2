package org.andy.code.dataStructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.Spesen;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SpesenRepository {

	public List<Spesen> findAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM Spesen s ORDER BY s.id", Spesen.class).list();
		}
	}

	// alle Einträge zu einem Datum
	public List<Spesen> findByDate(LocalDate date) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM Spesen s WHERE s.date = :date ORDER BY s.timeStart", Spesen.class)
					.setParameter("date", date).list();
		}
	}

	// optional: ein Eintrag, falls Datum eindeutig sein soll
	public Optional<Spesen> findOneByDate(LocalDate date) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM Spesen s WHERE s.date = :date", Spesen.class).setParameter("date", date)
					.uniqueResultOptional();
		}
	}

	public void save(Spesen spesen) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.persist(spesen); // INSERT
			tx.commit();
		}
	}

	public Spesen update(Spesen spesen) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			Spesen merged = session.merge(spesen); // UPDATE anhand PK
			tx.commit();
			return merged;
		}
	}

	// optional: Datumsspanne
	public List<Spesen> findByDateBetween(LocalDate from, LocalDate to) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM Spesen s WHERE s.date BETWEEN :from AND :to ORDER BY s.date, s.timeStart",	Spesen.class)
					.setParameter("from", from)
					.setParameter("to", to).list();
		}
	}
	
	public List<Spesen> findByDateBetweenAndUser(LocalDate from, LocalDate to, String user) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        return session.createQuery(
	            "FROM Spesen s WHERE s.user = :user AND s.date BETWEEN :from AND :to ORDER BY s.date, s.timeStart", 
	            Spesen.class)
	            .setParameter("user", user)
	            .setParameter("from", from)
	            .setParameter("to", to)
	            .list();
	    }
	}
}
