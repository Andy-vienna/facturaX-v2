package org.andy.code.dataStructure.repository;

import java.util.List;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.Owner;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OwnerRepository {

    public List<Owner> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Owner", Owner.class).list();
        }
    }

    public void insert(Owner owner) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(owner);
            tx.commit();
        }
    }

    public void update(Owner owner) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(owner);
            tx.commit();
        }
    }

}

