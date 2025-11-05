package org.andy.datamigration.code.dataStructure.repositoryProductive;

import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BestellungRepositoryMig {

    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                String sql =
                    url.startsWith("jdbc:postgresql")
                        ? "TRUNCATE TABLE tblbe RESTART IDENTITY CASCADE"
                        : "DELETE FROM tblbe"; // MSSQL
                try (java.sql.Statement st = conn.createStatement()) {
                    st.executeUpdate(sql);
                }
            });
            tx.commit();
        }
    }

    public void save(Bestellung bestellung) {
        Transaction tx = null;
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
            tx = session.beginTransaction();
            session.persist(bestellung);
            tx.commit();
        }
    }

}

