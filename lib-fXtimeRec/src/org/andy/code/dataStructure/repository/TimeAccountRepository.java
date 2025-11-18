package org.andy.code.dataStructure.repository;

import java.util.List;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.TimeAccount;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jakarta.persistence.NoResultException;

public class TimeAccountRepository {
	
	public List<TimeAccount> findAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM TimeAccount s", TimeAccount.class).list();
		}
	}
	
	public List<String> findUsers() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("select distinct e.username from TimeAccount e where e.username is not null",
				    String.class
				).getResultList();
		}
	}
	
	public TimeAccount findByUser(String userName) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM TimeAccount s where s.username = :u", TimeAccount.class)
                .setParameter("u", userName)
                .getSingleResult();
        } catch (NoResultException e) {
        	return null;
        }
	}
	
	public void save(TimeAccount helper) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.persist(helper); // INSERT
			tx.commit();
		}
	}

	public TimeAccount update(TimeAccount helper) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			TimeAccount merged = session.merge(helper); // UPDATE anhand PK
			tx.commit();
			return merged;
		}
	}

}
