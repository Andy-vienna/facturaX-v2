package org.andy.code.dataStructure.repository;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.WorkTimeRaw;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.OffsetDateTime;

public class WorkTimeRawRepository {

    /** Ereignis speichern: IN | BREAK_START | BREAK_END | OUT */
    public WorkTimeRaw record(String event, String source, String tz, OffsetDateTime ts, String user, String deviceId) {
    	WorkTimeRaw wtr = new WorkTimeRaw();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            wtr.setEvent(event);
            wtr.setUserName(user);
            wtr.setSource(source);
            wtr.setDeviceId(deviceId);
            wtr.setTimeZoneId(tz);
            wtr.setTs(ts);
            
            session.persist(wtr);
            tx.commit();
            
            return findLastEvent(user);
            
        }
    }

    /** Letztes Event eines Users. Nützlich für UI-Status. */
    public WorkTimeRaw findLastEvent(String userName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from WorkTimeRaw e where e.userName = :u order by e.id desc",
                    WorkTimeRaw.class)
                .setParameter("u", userName)
                .setMaxResults(1)
                .uniqueResult();
        }
    }
}
