package org.andy.code.dataStructure.repository;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.WorkTimeSheet;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jakarta.persistence.NoResultException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WorkTimeSheetRepository {
	
	public List<WorkTimeSheet> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM WorkTimeSheet ORDER BY id", WorkTimeSheet.class).list();
        }
    }

    public List<WorkTimeSheet> findAllByJahr(int jahr) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM WorkTimeSheet r WHERE r.jahr = :jahr ORDER BY monat", WorkTimeSheet.class)
                    .setParameter("jahr", jahr)
                    .getResultList();
        }
    }
    
    public WorkTimeSheet findByUserYearMonth(String user, int year, int month){
    	try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM WorkTimeSheet r WHERE r.jahr = :year AND r.monat = :month AND r.userName = :username", WorkTimeSheet.class)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .setParameter("username", user)
                    .getSingleResult();
        } catch (NoResultException e) {
        	return new WorkTimeSheet();
        }
    }
    
    public List<WorkTimeSheet> findByUserYear(String user, int year){
    	try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM WorkTimeSheet r WHERE r.jahr = :year AND r.userName = :username ORDER BY monat", WorkTimeSheet.class)
                    .setParameter("year", year)
                    .setParameter("username", user)
                    .list();
        }
    }

    public void save(WorkTimeSheet wts) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(wts);
            tx.commit();
        }
    }
    
    public void exportFile(String user, int year, int month, Path targetDir) throws Exception {
    	WorkTimeSheet wts = findByUserYearMonth(user, year, month);
        if (wts == null) throw new IllegalArgumentException("Keine Datei mit Jahr=" + year + " Monat=" + month + " gefunden");
        byte[] data = wts.getDatei();
        if (data == null) throw new IllegalStateException("Dateiinhalt ist NULL");

        Files.createDirectories(targetDir);

        String name = (wts.getDateiname() == null || wts.getDateiname().isBlank())
                ? "unbenannt.bin"
                : sanitize(wts.getDateiname());
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

