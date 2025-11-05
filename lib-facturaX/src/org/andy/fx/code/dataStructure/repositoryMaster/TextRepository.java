package org.andy.fx.code.dataStructure.repositoryMaster;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityMaster.Text;

public class TextRepository {

    public List<Text> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            return session.createQuery("FROM Text ORDER BY id", Text.class).list();
        }
    }
    
    public Text findById(int id) {
    	try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
    		return session.find(Text.class, id);
    	}
    	
    }

    public void update(Text text) {
        try (Session session = HibernateUtil.getSessionFactoryDb1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(text);
            tx.commit();
        }
    }

}

