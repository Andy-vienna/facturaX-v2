package org.andy.code.dataStructure.repository;

import java.util.List;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.Employee;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jakarta.persistence.NoResultException;

public class EmployeeRepository {
	
	public List<Employee> findAll() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("FROM Employee s", Employee.class).list();
		}
	}
	
	public List<String> findUsers() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("select distinct e.username from Employee e where e.username is not null",
				    String.class
				).getResultList();
		}
	}
	
	public Employee findByUser(String userName) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee s where s.username = :u", Employee.class)
                .setParameter("u", userName)
                .getSingleResult();
        } catch (NoResultException e) {
        	return null;
        }
	}
	
	public void save(Employee em) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.persist(em); // INSERT
			tx.commit();
		}
	}

	public Employee update(Employee em) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			Employee merged = session.merge(em); // UPDATE anhand PK
			tx.commit();
			return merged;
		}
	}
	
	public void delete(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Employee em = session.find(Employee.class, id);
            if (em != null) session.remove(em);
            tx.commit();
        }
    }

}
