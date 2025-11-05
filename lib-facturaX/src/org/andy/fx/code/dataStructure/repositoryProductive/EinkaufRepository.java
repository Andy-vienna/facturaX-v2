package org.andy.fx.code.dataStructure.repositoryProductive;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.Einkauf;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EinkaufRepository {
	
	public List<Einkauf> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM Einkauf", Einkauf.class).list();
        }
    }

    public List<Einkauf> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Einkauf r WHERE r.jahr = :jahr ORDER BY r.reDatum", Einkauf.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Einkauf findById(String id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Einkauf r WHERE r.id = :id", Einkauf.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(Einkauf einkauf) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(einkauf);
            tx.commit();
        }
    }

    public void update(Einkauf einkauf) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(einkauf);
            tx.commit();
        }
    }
    
    public void exportFileById(String id, Path targetDir) throws Exception {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            Einkauf einkauf = session.find(Einkauf.class, id);
            if (einkauf == null) throw new IllegalArgumentException("Keine Datei mit Id=" + id + " gefunden");
            byte[] data = einkauf.getDatei();
            if (data == null) throw new IllegalStateException("Dateiinhalt ist NULL");

            Files.createDirectories(targetDir);

            String name = (einkauf.getDateiname() == null || einkauf.getDateiname().isBlank())
                    ? "unbenannt.bin"
                    : sanitize(einkauf.getDateiname());
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

