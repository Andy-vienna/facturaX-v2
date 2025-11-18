package org.andy.code.dataStructure.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.andy.code.dataStructure.HibernateUtil;
import org.andy.code.dataStructure.entity.WorkTime;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WorkTimeRepository {

	public List<WorkTime> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM WorkTime ORDER BY idNummer", WorkTime.class).list();
        }
    }
	
	public List<String> findUsers() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			return session.createQuery("select distinct e.username from WorkTime e where e.username is not null",
				    String.class
				).getResultList();
		}
	}
	
	public WorkTime findById(long id){
    	try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM WorkTime r WHERE r.id = :id", WorkTime.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    public List<WorkTime> findDaysForUser(LocalDate dateStart, LocalDate dateEnd, String userName) {
    	ZoneId zone = ZoneId.systemDefault();
		OffsetDateTime start = dateStart.atStartOfDay(zone).toOffsetDateTime();
		OffsetDateTime end   = dateEnd.plusDays(1).atStartOfDay(zone).toOffsetDateTime();
    	
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from WorkTime e " +
                    "where e.username = :u and e.tsIn >= :s and e.tsIn < :e " +
                    "order by e.tsIn",
                    WorkTime.class)
                .setParameter("u", userName)
                .setParameter("s", start)
                .setParameter("e", end)
                .getResultList();
        }
    }
    
    public List<WorkTime> findDayForUser(LocalDate date, String userName) {
    	ZoneId zone = ZoneId.systemDefault();
		OffsetDateTime start = date.atStartOfDay(zone).toOffsetDateTime();
		OffsetDateTime end   = date.plusDays(1).atStartOfDay(zone).toOffsetDateTime();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from WorkTime e " +
                    "where e.username = :u and e.tsIn >= :s and e.tsIn < :e " +
                    "order by e.tsIn",
                    WorkTime.class)
                .setParameter("u", userName)
                .setParameter("s", start)
                .setParameter("e", end)
                .getResultList();
        }
    }
    
    public void save(WorkTime wt) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			session.persist(wt); // INSERT
			tx.commit();
		}
	}
    
    public WorkTime update(WorkTime wt) {
		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			tx = session.beginTransaction();
			WorkTime merged = session.merge(wt); // UPDATE anhand PK
			tx.commit();
			return merged;
		}
	}
    
    public void delete(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            WorkTime wt = session.find(WorkTime.class, id);
            if (wt != null) session.remove(wt);
            tx.commit();
        }
    }

}
