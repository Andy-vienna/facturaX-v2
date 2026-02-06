package org.andy.code.dataStructure.repository;

import java.util.List;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.Helper;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jakarta.persistence.NoResultException;

public class HelperRepository {
	
	public List<Helper> findAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM Helper s", Helper.class).list();
		}
	}
	
	public Helper findByUserAndYear(String userName, int year) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        return session.createQuery("FROM Helper s WHERE s.userName = :u AND s.year = :y", Helper.class)
	            .setParameter("u", userName)
	            .setParameter("y", year)
	            .getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}
	
	public void save(Helper helper) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.persist(helper); // INSERT
			tx.commit();
		}
	}

	public Helper update(Helper helper) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			Helper merged = session.merge(helper); // UPDATE anhand PK
			tx.commit();
			return merged;
		}
	}

}
