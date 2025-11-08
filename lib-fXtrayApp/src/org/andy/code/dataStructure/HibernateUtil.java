package org.andy.code.dataStructure;

import org.andy.code.dataStructure.entity.WorkTimeRaw;
import org.andy.code.main.Einstellungen;
import org.andy.code.misc.App;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        // Logging festlegen
        Configurator.initialize(new DefaultConfiguration()); // Console-Appender aktiv
        Configurator.setRootLevel(Level.WARN);

        // Kategorien NACH der Initialisierung setzen
        Configurator.setLevel("org.hibernate.SQL", Level.WARN);
        Configurator.setLevel("org.hibernate.orm.jdbc.bind", Level.WARN);
        Configurator.setLevel("org.hibernate.orm.jdbc.extract", Level.WARN);
        Configurator.setLevel("org.hibernate.type.descriptor.jdbc", Level.WARN);
        Configurator.setLevel("org.hibernate.orm.jdbc.lob", Level.WARN);
        Configurator.setLevel("org.hibernate.loader.entity", Level.WARN);

        // Hibernate initialisieren
        sessionFactory = buildSessionFactory();
        
        App.setDB(sessionFactory.openSession().doReturningWork(c -> c.getMetaData().getDatabaseProductName()) + " " +
        		sessionFactory.openSession().doReturningWork(c -> c.getMetaData().getDatabaseProductVersion()));
    }

    private static SessionFactory buildSessionFactory() {
        Map<String, Object> settings = new HashMap<>();

        // ---- HikariCP aktivieren ----
        settings.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        // ---- HikariCP-Einstellungen (DB2) ----
        settings.put("hibernate.hikari.jdbcUrl", Einstellungen.getsData());
        settings.put("hibernate.hikari.username", Einstellungen.getDbSettings().dbUser);
        settings.put("hibernate.hikari.password", Einstellungen.getDbSettings().dbPass);

        settings.put("hibernate.hikari.maximumPoolSize", "10");
        settings.put("hibernate.hikari.minimumIdle", "2");
        settings.put("hibernate.hikari.idleTimeout", "300000");
        settings.put("hibernate.hikari.maxLifetime", "1800000");
        settings.put("hibernate.hikari.connectionTimeout", "30000");
        settings.put("hibernate.hikari.poolName", "FX-DB2");

        // Hibernate
        settings.put("hibernate.show_sql", "false");
        settings.put("hibernate.format_sql", "false");
        settings.put("hibernate.hbm2ddl.auto", Einstellungen.getDbSettings().dbMode);

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        MetadataSources sources = new MetadataSources(serviceRegistry);
        sources.addAnnotatedClass(WorkTimeRaw.class);

        Metadata metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    public static SessionFactory getSessionFactory() { return sessionFactory; }
}
