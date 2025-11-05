package org.andy.datamigration.code.dataStructure.repositoryProductive;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;

public class FileStoreRepositoryMig {
	
	public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                String sql =
                    url.startsWith("jdbc:postgresql")
                        ? "TRUNCATE TABLE tblfiles RESTART IDENTITY CASCADE"
                        : "DELETE FROM tblfiles"; // MSSQL
                try (java.sql.Statement st = conn.createStatement()) {
                    st.executeUpdate(sql);
                }
            });
            tx.commit();
        }
    }

    public void save(FileStore file) {
        Transaction tx = null;
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig2().openSession()) {
            tx = session.beginTransaction();
            session.persist(file);  // bei Neuanlage
            tx.commit();
        }
    }

}

