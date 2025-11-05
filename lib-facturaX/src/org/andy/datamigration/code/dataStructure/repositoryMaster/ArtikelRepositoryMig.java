package org.andy.datamigration.code.dataStructure.repositoryMaster;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.andy.datamigration.code.dataStructure.HibernateUtilMigration;
import org.andy.fx.code.dataStructure.entityMaster.Artikel;

public class ArtikelRepositoryMig {

    public void deleteAllData() {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.doWork(conn -> {
                String url = conn.getMetaData().getURL();
                String sql =
                    url.startsWith("jdbc:postgresql")
                        ? "TRUNCATE TABLE tblartikel RESTART IDENTITY CASCADE"
                        : "DELETE FROM tblartikel"; // MSSQL
                try (java.sql.Statement st = conn.createStatement()) {
                    st.executeUpdate(sql);
                }
            });
            tx.commit();
        }
    }

    public void insert(Artikel artikel) {
        try (Session session = HibernateUtilMigration.getSessionFactoryDbMig1().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(artikel);
            tx.commit();
        }
    }

}

