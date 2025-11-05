package org.andy.code.dataStructure.repository;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.WorkTime;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class WorkTimeRepository {

    private final String deviceId;
    private String user = null;

    public WorkTimeRepository(String deviceId, String user) {
        this.deviceId = deviceId;
        this.user = user;
    }

    /** Ereignis speichern: IN | BREAK_START | BREAK_END | OUT */
    public void record(String type, String note) {
    	WorkTime e = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            LocalDateTime now = LocalDateTime.now(); // Windows-Systemzeit

            if (type.equals("IN")) {
            	e = new WorkTime();
            } else {
            	e = findLastEvent(user);
            }
            
            e.setUserName(user);
            switch(type) {
            case "IN"          -> e.setTsLocalIN(now);
            case "BREAK_START" -> e.setTsLocalBS(now);
            case "BREAK_END"   -> e.setTsLocalBE(now);
            case "OUT"         -> e.setTsLocalOUT(now);
            }
            e.setLastEvent(type);
            e.setNote(note);
            e.setSource("DESKTOP");
            e.setDeviceId(deviceId);

            if (type.equals("IN")) {
            	session.persist(e);
            } else {
            	@SuppressWarnings("unused")
				WorkTime merged = session.merge(e); // UPDATE anhand PK
            }
            tx.commit();
        }
    }

    /** Alle heutigen Events eines Users in Zeitreihenfolge. */
    public List<WorkTime> findTodayForUser(String userName) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from WorkTime e " +
                    "where e.userName = :u and e.tsLocal >= :s and e.tsLocal < :e " +
                    "order by e.tsLocal",
                    WorkTime.class)
                .setParameter("u", userName)
                .setParameter("s", start)
                .setParameter("e", end)
                .getResultList();
        }
    }

    /** Letztes Event eines Users. Nützlich für UI-Status. */
    public WorkTime findLastEvent(String userName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from WorkTime e where e.userName = :u order by e.id desc",
                    WorkTime.class)
                .setParameter("u", userName)
                .setMaxResults(1)
                .uniqueResult();
        }
    }
}
