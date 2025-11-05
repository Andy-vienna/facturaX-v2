package org.andy.datamigration.code.dataStructure.repositoryProductive;

import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.datamigration.code.dataStructure.entityProductive.SVSteuerMig;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SVSteuerRepositoryMig {

    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                try (java.sql.Statement st = conn.createStatement()) {
                    if (url.startsWith("jdbc:postgresql")) {
                    	st.addBatch("TRUNCATE TABLE tblst RESTART IDENTITY CASCADE");
                        st.addBatch("ALTER TABLE tblst ALTER COLUMN id RESTART WITH 1"); // 2. Zeile
                    } else {
                        st.addBatch("DELETE FROM tblst"); // MSSQL
                        st.addBatch("DBCC CHECKIDENT ('dbo.tblst', RESEED, 0)"); // optional: nächster Wert = 1
                    }
                    st.executeBatch();
                }
            });
            tx.commit();
        }
    }

    public void save(SVSteuerMig svsteuer) {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
        	Transaction tx = session.beginTransaction();
            session.merge(svsteuer);
            tx.commit();
        }
    }

}

