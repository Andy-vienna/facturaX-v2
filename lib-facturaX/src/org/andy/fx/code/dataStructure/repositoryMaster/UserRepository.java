package org.andy.fx.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityMaster.User;

public class UserRepository {

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM User ORDER BY id", User.class).list();
        }
    }
    
    public User findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery(
                    "FROM User r WHERE r.id = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }
    
    public User findByEmail(String email) {
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery(
                    "FROM User r WHERE r.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }
	}

    public void insert(User user) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        }
    }

    public void update(User user) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        }
    }

    public void delete(String id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) session.remove(user);
            tx.commit();
        }
    }

	
}

