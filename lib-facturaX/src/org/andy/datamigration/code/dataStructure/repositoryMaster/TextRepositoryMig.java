package org.andy.datamigration.code.dataStructure.repositoryMaster;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.fx.code.dataStructure.entityMaster.Text;

public class TextRepositoryMig {

    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                String sql =
                    url.startsWith("jdbc:postgresql")
                        ? "TRUNCATE TABLE tbltext RESTART IDENTITY CASCADE"
                        : "DELETE FROM tbltext"; // MSSQL
                try (java.sql.Statement st = conn.createStatement()) {
                    st.executeUpdate(sql);
                }
            });
            tx.commit();
        }
    }
    
    public void insert(Text text) {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(text);
            tx.commit();
        }
    }

}

