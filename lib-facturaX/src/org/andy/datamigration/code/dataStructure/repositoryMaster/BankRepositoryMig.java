package org.andy.datamigration.code.dataStructure.repositoryMaster;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.datamigration.code.dataStructure.entityMaster.BankMig;

public class BankRepositoryMig {

    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                try (java.sql.Statement st = conn.createStatement()) {
                    if (url.startsWith("jdbc:postgresql")) {
                    	st.addBatch("TRUNCATE TABLE tblbank RESTART IDENTITY CASCADE");
                        st.addBatch("ALTER TABLE tblbank ALTER COLUMN id RESTART WITH 1"); // 2. Zeile
                    } else {
                        st.addBatch("DELETE FROM tblbank"); // MSSQL
                        st.addBatch("DBCC CHECKIDENT ('dbo.tblbank', RESEED, 0)"); // optional: nächster Wert = 1
                    }
                    st.executeBatch();
                }
            });
            tx.commit();
        }
    }

    public void insert(BankMig bank) {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(bank);
            tx.commit();
        }
    }

}

