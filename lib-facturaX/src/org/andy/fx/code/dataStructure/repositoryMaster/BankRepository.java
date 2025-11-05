package org.andy.fx.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityMaster.Bank;

public class BankRepository {

    public List<Bank> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Bank ORDER BY id", Bank.class).list();
        }
    }

    public void insert(Bank bank) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(bank);
            tx.commit();
        }
    }

    public void update(Bank bank, int id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            bank.setId(id);
            session.merge(bank);
            tx.commit();
        }
    }

    public void delete(int id) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            Bank bank = session.find(Bank.class, id);
            if (bank != null) session.remove(bank);
            tx.commit();
        }
    }
}

