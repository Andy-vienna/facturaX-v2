package org.andy.fx.code.dataStructure.repositoryProductive;

import java.util.List;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.Helper;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HelperRepository {
	
	public List<Helper> findAll() {
		try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
			return session.createQuery("FROM Helper s", Helper.class).list();
		}
	}
	
	public void save(Helper helper) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
			tx = session.beginTransaction();
			session.persist(helper); // INSERT
			tx.commit();
		}
	}

	public Helper update(Helper helper) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
			tx = session.beginTransaction();
			Helper merged = session.merge(helper); // UPDATE anhand PK
			tx.commit();
			return merged;
		}
	}

}
