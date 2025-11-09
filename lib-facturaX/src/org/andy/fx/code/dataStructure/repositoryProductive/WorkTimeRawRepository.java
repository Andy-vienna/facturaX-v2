package org.andy.fx.code.dataStructure.repositoryProductive;

import java.time.OffsetDateTime;
import java.util.List;

import org.andy.fx.code.dataStructure.HibernateUtil;
import org.andy.fx.code.dataStructure.entityProductive.WorkTimeRaw;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WorkTimeRawRepository {
	
	public List<WorkTimeRaw> findDaysForUser(OffsetDateTime dateStart, OffsetDateTime dateEnd, String userName) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
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
	
	public void delete(long id) {
        try (Session session = HibernateUtil.getSessionFactoryDb2().openSession()) {
            Transaction tx = session.beginTransaction();
            WorkTimeRaw wtr = session.find(WorkTimeRaw.class, id);
            if (wtr != null) session.remove(wtr);
            tx.commit();
        }
    }

}
