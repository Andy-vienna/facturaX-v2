package org.andy.fx.code.dataStructure.repositoryProductive;

import org.hibernate.Session;
import org.hibernate.Transaction;

import static org.andy.fx.code.dataStructure.HibernateUtil.getSessionFactoryDb2;

import java.util.List;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;

public class FileStoreRepository {
	
	public List<FileStore> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM FileStore", FileStore.class).list();
        }
    }
	
	public FileStore findById(String id) {
        try (Session session = getSessionFactoryDb2().openSession()) {
        	return session.createQuery(
                    "FROM FileStore r WHERE r.idNummer = :id", FileStore.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(FileStore file) {
        Transaction tx = null;
        try (Session session = getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(file);  // bei Neuanlage
            tx.commit();
        }
    }

    public void update(FileStore file) {
        Transaction tx = null;
        try (Session session = getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(file);  // ersetzt vorhandene Daten
            tx.commit();
        }
    }

    public void delete(String idNummer) {
        Transaction tx = null;
        try (Session session = getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            FileStore file = session.find(FileStore.class, idNummer);
            if (file != null) {
                session.remove(file);
            }
            tx.commit();
        }
    }
}

