package org.andy.datamigration.code.dataStructure.repositoryProductive;

import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.datamigration.code.dataStructure.entityProductive.AusgabenMig;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AusgabenRepositoryMig {

    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                try (java.sql.Statement st = conn.createStatement()) {
                    if (url.startsWith("jdbc:postgresql")) {
                    	st.addBatch("TRUNCATE TABLE tblex RESTART IDENTITY CASCADE");
                        st.addBatch("ALTER TABLE tblex ALTER COLUMN id RESTART WITH 1"); // 2. Zeile
                    } else {
                        st.addBatch("DELETE FROM tblex"); // MSSQL
                        st.addBatch("DBCC CHECKIDENT ('dbo.tblex', RESEED, 0)"); // optional: nächster Wert = 1
                    }
                    st.executeBatch();
                }
            });
            tx.commit();
        }
    }

    public void save(AusgabenMig ausgaben) {
    	try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(ausgaben);
            tx.commit();
        }
    }

}

