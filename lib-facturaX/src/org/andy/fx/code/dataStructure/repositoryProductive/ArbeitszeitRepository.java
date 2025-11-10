package org.andy.fx.code.dataStructure.repositoryProductive;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.Arbeitszeit;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ArbeitszeitRepository {
	
	public List<Arbeitszeit> findAll() {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery("FROM Arbeitszeit ORDER BY id", Arbeitszeit.class).list();
        }
    }

    public List<Arbeitszeit> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Arbeitszeit r WHERE r.jahr = :jahr ORDER BY monat", Arbeitszeit.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public Arbeitszeit findByYearMonth(String user, int year, int month){
    	try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            return session.createQuery(
                    "FROM Arbeitszeit r WHERE r.jahr = :year AND r.monat = :month AND r.userName = :username", Arbeitszeit.class)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .setParameter("username", user)
                    .getSingleResult();
        }
    }

    public void save(Arbeitszeit arbeitszeit) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            tx = session.beginTransaction();
            session.persist(arbeitszeit);
            tx.commit();
        }
    }
    
    public void exportFile(String user, int year, int month, Path targetDir) throws Exception {
    	Arbeitszeit arbeitszeit = findByYearMonth(user, year, month);
        if (arbeitszeit == null) throw new IllegalArgumentException("Keine Datei mit Jahr=" + year + " Monat=" + month + " gefunden");
        byte[] data = arbeitszeit.getDatei();
        if (data == null) throw new IllegalStateException("Dateiinhalt ist NULL");

        Files.createDirectories(targetDir);

        String name = (arbeitszeit.getDateiname() == null || arbeitszeit.getDateiname().isBlank())
                ? "unbenannt.bin"
                : sanitize(arbeitszeit.getDateiname());
        Path out = unique(targetDir.resolve(name));
        Files.write(out, data);   // lädt alles in den Speicher
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

