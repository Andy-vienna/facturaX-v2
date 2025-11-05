package org.andy.fx.code.dataStructure;

import org.andy.fx.code.dataStructure.entityMaster.Artikel;
import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Gwb;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.entityMaster.Owner;
import org.andy.fx.code.dataStructure.entityMaster.Tax;
import org.andy.fx.code.dataStructure.entityMaster.Text;
import org.andy.fx.code.dataStructure.entityMaster.User;
import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.entityProductive.Arbeitszeit;
import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.andy.fx.code.dataStructure.entityProductive.Einkauf;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.entityProductive.Helper;
import org.andy.fx.code.dataStructure.entityProductive.Lieferschein;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.entityProductive.SVSteuer;
import org.andy.fx.code.dataStructure.entityProductive.Spesen;
import org.andy.fx.code.dataStructure.entityProductive.WorkTime;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.App;
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

    private static SessionFactory sessionFactoryDb1;
    private static SessionFactory sessionFactoryDb2;

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
        sessionFactoryDb1 = buildSessionFactoryDb1();
        sessionFactoryDb2 = buildSessionFactoryDb2();
        
        App.setDB(sessionFactoryDb1.openSession().doReturningWork(c -> c.getMetaData().getDatabaseProductName()) + " " +
        		sessionFactoryDb1.openSession().doReturningWork(c -> c.getMetaData().getDatabaseProductVersion()));
    }

    private static SessionFactory buildSessionFactoryDb1() {
        Map<String, Object> settings = new HashMap<>();

        // ---- HikariCP aktivieren ----
        settings.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        // Dialekt (optional, aber bei SQL Server sinnvoll)
        //switch(Einstellungen.getDbSettings().dbType) {
        //	case "mssql" -> settings.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        //	case "postgre" -> settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        //}

        // ---- HikariCP-Einstellungen (DB1) ----
        settings.put("hibernate.hikari.jdbcUrl", Einstellungen.getsMasterData());
        settings.put("hibernate.hikari.username", Einstellungen.getDbSettings().dbUser);
        settings.put("hibernate.hikari.password", Einstellungen.getDbSettings().dbPass);

        // Pooling-Parameter – an deine Last anpassen
        settings.put("hibernate.hikari.maximumPoolSize", "10");
        settings.put("hibernate.hikari.minimumIdle", "2");
        settings.put("hibernate.hikari.idleTimeout", "300000");   // 5 min
        settings.put("hibernate.hikari.maxLifetime", "1800000");  // 30 min
        settings.put("hibernate.hikari.connectionTimeout", "30000");
        settings.put("hibernate.hikari.poolName", "FX-DB1");

        // Hibernate
        settings.put("hibernate.show_sql", "false");
        settings.put("hibernate.format_sql", "false");
        settings.put("hibernate.hbm2ddl.auto", Einstellungen.getDbSettings().dbMode); // wie bisher

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        MetadataSources sources = new MetadataSources(serviceRegistry);
        sources.addAnnotatedClass(Artikel.class);
        sources.addAnnotatedClass(Bank.class);
        sources.addAnnotatedClass(Gwb.class);
        sources.addAnnotatedClass(Kunde.class);
        sources.addAnnotatedClass(Lieferant.class);
        sources.addAnnotatedClass(Owner.class);
        sources.addAnnotatedClass(Tax.class);
        sources.addAnnotatedClass(Text.class);
        sources.addAnnotatedClass(User.class);
        
        Metadata metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    private static SessionFactory buildSessionFactoryDb2() {
        Map<String, Object> settings = new HashMap<>();

        // ---- HikariCP aktivieren ----
        settings.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        // Dialekt (optional, aber bei SQL Server sinnvoll)
        //switch(Einstellungen.getDbSettings().dbType) {
        //	case "mssql" -> settings.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        //	case "postgre" -> settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        //}

        // ---- HikariCP-Einstellungen (DB2) ----
        settings.put("hibernate.hikari.jdbcUrl", Einstellungen.getsProductiveData());
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
        sources.addAnnotatedClass(Helper.class);
        sources.addAnnotatedClass(Angebot.class);
        sources.addAnnotatedClass(Rechnung.class);
        sources.addAnnotatedClass(Bestellung.class);
        sources.addAnnotatedClass(Lieferschein.class);
        sources.addAnnotatedClass(FileStore.class);
        sources.addAnnotatedClass(Einkauf.class);
        sources.addAnnotatedClass(Ausgaben.class);
        sources.addAnnotatedClass(Spesen.class);
        sources.addAnnotatedClass(WorkTime.class);
        sources.addAnnotatedClass(Arbeitszeit.class);
        sources.addAnnotatedClass(SVSteuer.class);

        Metadata metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    public static SessionFactory getSessionFactoryDb1() { return sessionFactoryDb1; }
    public static SessionFactory getSessionFactoryDb2() { return sessionFactoryDb2; }
}
