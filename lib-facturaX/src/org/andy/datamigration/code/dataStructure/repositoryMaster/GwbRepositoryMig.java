package org.andy.datamigration.code.dataStructure.repositoryMaster;

import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.fx.code.dataStructure.entityMaster.Gwb;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class GwbRepositoryMig {
    
    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                String sql =
                    url.startsWith("jdbc:postgresql")
                        ? "TRUNCATE TABLE tblgwbvalue RESTART IDENTITY CASCADE"
                        : "DELETE FROM tblgwbvalue"; // MSSQL
                try (java.sql.Statement st = conn.createStatement()) {
                    st.executeUpdate(sql);
                }
            });
            tx.commit();
        }
    }

    public void insert(Gwb gwb) {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(gwb);
            tx.commit();
        }
    }

}

