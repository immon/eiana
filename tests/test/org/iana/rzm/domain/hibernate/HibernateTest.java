package org.iana.rzm.domain.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.iana.rzm.conf.SpringApplicationContext;

/**
 * @author Jakub Laszkiewicz
 */
abstract public class HibernateTest {
    protected Session session;
    protected Transaction tx = null;
    protected SessionFactory sessionFactory = (SessionFactory) SpringApplicationContext.getInstance().getContext().getBean("sessionFactory");

    protected void begin() {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
    }

    protected void close() {
        if (tx != null) {
            tx.commit();
            tx = null;
        }
        session.close();
    }

    protected void beginTx() {
        tx = session.beginTransaction();
    }

    protected void closeTx() {
        tx.commit();
        tx = null;
    }

    protected static interface HibernateSeq {
        void run(Session session) throws HibernateException;
    }

    protected final void runInTransaction(HibernateSeq seq) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            seq.run(session);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
