package org.andy.fx.code.dataStructure.repositoryProductive;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.SVSteuer;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SVSteuerRepository {
	
	public List<SVSteuer> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM SVSteuer", SVSteuer.class).list();
        }
    }

    public List<SVSteuer> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM SVSteuer r WHERE r.jahr = :jahr ORDER BY r.datum", SVSteuer.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public SVSteuer findById(int id){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM SVSteuer r WHERE r.id = :id", SVSteuer.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public void save(SVSteuer svsteuer) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(svsteuer);
            tx.commit();
        }
    }

    public void update(SVSteuer svsteuer) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.merge(svsteuer);
            tx.commit();
        }
    }
    
    public void exportFileById(int id, Path targetDir) throws Exception {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            SVSteuer svsteuer = session.find(SVSteuer.class, id);
            if (svsteuer == null) throw new IllegalArgumentException("Keine Datei mit Id=" + id + " gefunden");
            byte[] data = svsteuer.getDatei();
            if (data == null) throw new IllegalStateException("Dateiinhalt ist NULL");

            Files.createDirectories(targetDir);

            String name = (svsteuer.getDateiname() == null || svsteuer.getDateiname().isBlank())
                    ? "unbenannt.bin"
                    : sanitize(svsteuer.getDateiname());
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

