package org.andy.fx.code.dataStructure.repositoryProductive;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AusgabenRepository {
	
	public List<Ausgaben> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM Ausgaben ORDER BY id", Ausgaben.class).list();
        }
    }

    public List<Ausgaben> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Ausgaben r WHERE r.jahr = :jahr ORDER BY r.datum", Ausgaben.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Ausgaben findById(int id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Ausgaben r WHERE r.id = :id", Ausgaben.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(Ausgaben ausgaben) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(ausgaben);
            tx.commit();
        }
    }

    public void update(Ausgaben ausgaben) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(ausgaben);
            tx.commit();
        }
    }
    
    public void exportFileById(int id, Path targetDir) throws Exception {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            Ausgaben ausgaben = session.find(Ausgaben.class, id);
            if (ausgaben == null) throw new IllegalArgumentException("Keine Datei mit Id=" + id + " gefunden");
            byte[] data = ausgaben.getDatei();
            if (data == null) throw new IllegalStateException("Dateiinhalt ist NULL");

            Files.createDirectories(targetDir);

            String name = (ausgaben.getDateiname() == null || ausgaben.getDateiname().isBlank())
                    ? "unbenannt.bin"
                    : sanitize(ausgaben.getDateiname());
            Path out = unique(targetDir.resolve(name));
            Files.write(out, data);   // lädt alles in den Speicher
        }
    }
    
    private static String sanitize(String s) {
        return s.replaceAll("[\\\\/:*?\"<>|]", "_").strip();
    }
    
    private static Path unique(Path p) throws java.io.IOException {
        if (!Files.exists(p)) return p;
        String file = p.getFileName().toString();
        int dot = file.lastIndexOf('.');
        String base = (dot > 0) ? file.substring(0, dot) : file;
        String ext  = (dot > 0) ? file.substring(dot)    : "";
        for (int i = 1; i < 10_000; i++) {
            Path cand = p.getParent().resolve(base + " (" + i + ")" + ext);
            if (!Files.exists(cand)) return cand;
        }
        throw new java.io.IOException("Zu viele Kollisionen bei " + p);
    }
}

