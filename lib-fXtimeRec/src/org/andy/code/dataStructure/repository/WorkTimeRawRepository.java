package org.andy.code.dataStructure.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.WorkTimeRaw;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WorkTimeRawRepository {
	
	public List<WorkTimeRaw> findDaysForUser(OffsetDateTime dateStart, OffsetDateTime dateEnd, String userName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from WorkTimeRaw e " +
                    "where e.userName = :u and e.ts >= :s and e.ts < :e " +
                    "order by e.ts",
                    WorkTimeRaw.class)
                .setParameter("u", userName)
                .setParameter("s", dateStart)
                .setParameter("e", dateEnd)
                .getResultList();
        }
    }
	
	public void save(WorkTimeRaw wt) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.persist(wt); // INSERT
			tx.commit();
		}
	}
	
	public void delete(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            WorkTimeRaw wtr = session.find(WorkTimeRaw.class, id);
            if (wtr != null) session.remove(wtr);
            tx.commit();
        }
    }

}
