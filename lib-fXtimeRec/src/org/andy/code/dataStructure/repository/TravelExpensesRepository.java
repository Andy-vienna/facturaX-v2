package org.andy.code.dataStructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.TravelExpenses;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TravelExpensesRepository {

	public List<TravelExpenses> findAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM TravelExpenses s ORDER BY s.id", TravelExpenses.class).list();
		}
	}

	// alle Einträge zu einem Datum
	public List<TravelExpenses> findByDate(LocalDate date) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM TravelExpenses s WHERE s.date = :date ORDER BY s.timeStart", TravelExpenses.class)
					.setParameter("date", date).list();
		}
	}

	// optional: ein Eintrag, falls Datum eindeutig sein soll
	public Optional<TravelExpenses> findOneByDate(LocalDate date) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM TravelExpenses s WHERE s.date = :date", TravelExpenses.class).setParameter("date", date)
					.uniqueResultOptional();
		}
	}

	public void save(TravelExpenses spesen) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.persist(spesen); // INSERT
			tx.commit();
		}
	}

	public TravelExpenses update(TravelExpenses spesen) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			TravelExpenses merged = session.merge(spesen); // UPDATE anhand PK
			tx.commit();
			return merged;
		}
	}

	// optional: Datumsspanne
	public List<TravelExpenses> findByDateBetween(LocalDate from, LocalDate to) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM TravelExpenses s WHERE s.date BETWEEN :from AND :to ORDER BY s.date, s.timeStart",	TravelExpenses.class)
					.setParameter("from", from)
					.setParameter("to", to).list();
		}
	}
	
	public List<TravelExpenses> findByDateBetweenAndUser(LocalDate from, LocalDate to, String user) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        return session.createQuery(
	            "FROM TravelExpenses s WHERE s.user = :user AND s.date BETWEEN :from AND :to ORDER BY s.date, s.timeStart", 
	            TravelExpenses.class)
	            .setParameter("user", user)
	            .setParameter("from", from)
	            .setParameter("to", to)
	            .list();
	    }
	}
}
