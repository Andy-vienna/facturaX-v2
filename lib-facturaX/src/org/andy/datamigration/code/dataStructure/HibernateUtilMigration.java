package org.andy.datamigration.code.dataStructure;

import java.util.HashMap;
import java.util.Map;

import org.andy.datamigration.code.dataStructure.entityMaster.BankMig;
import org.andy.datamigration.code.dataStructure.entityProductive.AusgabenMig;
import org.andy.datamigration.code.dataStructure.entityProductive.SVSteuerMig;
import org.andy.datamigration.gui.MigrationPanel;
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
import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.andy.fx.code.dataStructure.entityProductive.Einkauf;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.entityProductive.Lieferschein;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.entityProductive.SVSteuer;
import org.andy.fx.code.main.Einstellungen;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public final class HibernateUtilMigration {

    private static volatile SessionFactory sessionFactoryDbMig1;
    private static volatile SessionFactory sessionFactoryDbMig2;

    static {
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.WARN);
        Configurator.setLevel("org.hibernate.SQL", Level.WARN);
        Configurator.setLevel("org.hibernate.orm.jdbc.bind", Level.WARN);
        Configurator.setLevel("org.hibernate.orm.jdbc.extract", Level.WARN);
        Configurator.setLevel("org.hibernate.type.descriptor.jdbc", Level.WARN);
        Configurator.setLevel("org.hibernate.orm.jdbc.lob", Level.WARN);
        Configurator.setLevel("org.hibernate.loader.entity", Level.WARN);
        // KEIN build hier
    }

    private static void putIfNotBlank(Map<String,Object> m, String k, String v) {
        if (v != null && !v.isBlank()) m.put(k, v);
    }

    private static StandardServiceRegistry buildRegistry(Map<String,Object> settings) {
        // Pflichtwerte prüfen
        String[] required = {
            "hibernate.hikari.jdbcUrl",
            "hibernate.hikari.username",
            "hibernate.hikari.password"
        };
        for (String r : required) {
            Object v = settings.get(r);
            if (!(v instanceof String s) || s.isBlank())
                throw new IllegalStateException("Missing setting: " + r);
        }
        return new StandardServiceRegistryBuilder().applySettings(settings).build();
    }

    private static SessionFactory buildSessionFactoryDbMig1() {
        Map<String,Object> settings = new HashMap<>();
        settings.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        putIfNotBlank(settings, "hibernate.hikari.jdbcUrl", MigrationPanel.getMigMasterJDBC());
        putIfNotBlank(settings, "hibernate.hikari.username", Einstellungen.getDbSettings().dbUser);
        putIfNotBlank(settings, "hibernate.hikari.password", Einstellungen.getDbSettings().dbPass);

        settings.put("hibernate.hikari.maximumPoolSize", "10");
        settings.put("hibernate.hikari.minimumIdle", "2");
        settings.put("hibernate.hikari.idleTimeout", "300000");
        settings.put("hibernate.hikari.maxLifetime", "1800000");
        settings.put("hibernate.hikari.connectionTimeout", "30000");
        settings.put("hibernate.hikari.poolName", "FX-DB1");

        putIfNotBlank(settings, "hibernate.hbm2ddl.auto", Einstellungen.getDbSettings().dbMode);
        settings.put("hibernate.show_sql", "false");
        settings.put("hibernate.format_sql", "false");

        StandardServiceRegistry serviceRegistry = buildRegistry(settings);
        MetadataSources sources = new MetadataSources(serviceRegistry)
            .addAnnotatedClass(Artikel.class)
        	.addAnnotatedClass(Bank.class)
        	.addAnnotatedClass(BankMig.class)
        	.addAnnotatedClass(Gwb.class)
        	.addAnnotatedClass(Kunde.class)
        	.addAnnotatedClass(Lieferant.class)
        	.addAnnotatedClass(Owner.class)
        	.addAnnotatedClass(Tax.class)
        	.addAnnotatedClass(Text.class)
        	.addAnnotatedClass(User.class);
        Metadata metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    private static SessionFactory buildSessionFactoryDbMig2() {
        Map<String,Object> settings = new HashMap<>();
        settings.put("hibernate.connection.provider_class",
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        putIfNotBlank(settings, "hibernate.hikari.jdbcUrl", MigrationPanel.getMigWorkJDBC());
        putIfNotBlank(settings, "hibernate.hikari.username", Einstellungen.getDbSettings().dbUser);
        putIfNotBlank(settings, "hibernate.hikari.password", Einstellungen.getDbSettings().dbPass);

        settings.put("hibernate.hikari.maximumPoolSize", "10");
        settings.put("hibernate.hikari.minimumIdle", "2");
        settings.put("hibernate.hikari.idleTimeout", "300000");
        settings.put("hibernate.hikari.maxLifetime", "1800000");
        settings.put("hibernate.hikari.connectionTimeout", "30000");
        settings.put("hibernate.hikari.poolName", "FX-DB2");

        putIfNotBlank(settings, "hibernate.hbm2ddl.auto", Einstellungen.getDbSettings().dbMode);
        settings.put("hibernate.show_sql", "false");
        settings.put("hibernate.format_sql", "false");

        StandardServiceRegistry serviceRegistry = buildRegistry(settings);
        MetadataSources sources = new MetadataSources(serviceRegistry)
            .addAnnotatedClass(Angebot.class)
            .addAnnotatedClass(Rechnung.class)
            .addAnnotatedClass(Bestellung.class)
            .addAnnotatedClass(Lieferschein.class)
            .addAnnotatedClass(FileStore.class)
            .addAnnotatedClass(Einkauf.class)
            .addAnnotatedClass(Ausgaben.class)
            .addAnnotatedClass(AusgabenMig.class)
            .addAnnotatedClass(SVSteuer.class)
        	.addAnnotatedClass(SVSteuerMig.class);
        Metadata metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    public static SessionFactory getSessionFactoryDbMig1() {
        if (sessionFactoryDbMig1 == null) synchronized (HibernateUtilMigration.class) {
            if (sessionFactoryDbMig1 == null) sessionFactoryDbMig1 = buildSessionFactoryDbMig1();
        }
        return sessionFactoryDbMig1;
    }

    public static SessionFactory getSessionFactoryDbMig2() {
        if (sessionFactoryDbMig2 == null) synchronized (HibernateUtilMigration.class) {
            if (sessionFactoryDbMig2 == null) sessionFactoryDbMig2 = buildSessionFactoryDbMig2();
        }
        return sessionFactoryDbMig2;
    }
}
