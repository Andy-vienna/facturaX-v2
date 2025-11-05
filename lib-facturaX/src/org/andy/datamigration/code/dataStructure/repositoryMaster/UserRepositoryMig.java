package org.andy.datamigration.code.dataStructure.repositoryMaster;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.fx.code.dataStructure.entityMaster.User;

public class UserRepositoryMig {

    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                String sql =
                    url.startsWith("jdbc:postgresql")
                        ? "TRUNCATE TABLE tbluser RESTART IDENTITY CASCADE"
                        : "DELETE FROM tbluser"; // MSSQL
                try (java.sql.Statement st = conn.createStatement()) {
                    st.executeUpdate(sql);
                }
            });
            tx.commit();
        }
    }

    public void insert(User user) {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        }
    }

}

