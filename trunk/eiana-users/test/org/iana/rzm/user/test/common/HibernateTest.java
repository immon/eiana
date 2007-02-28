/**
 * org.iana.rzm.user.test.common.HibernateTest
 * (C) NASK 2006
 * jakubl, 2007-02-28 16:22:34
 */
package org.iana.rzm.user.test.common;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * @author Jakub Laszkiewicz
 */
abstract public class HibernateTest {
    protected Session session;
    protected Transaction tx;
    protected SessionFactory sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();

    protected void begin() {
        session = sessionFactory.openSession();
        tx = session.beginTransaction();
    }

    protected void close() {
        tx.commit();
        session.close();
    }

    protected void beginTx() {
        tx = session.beginTransaction();
    }

    protected void closeTx() {
        tx.commit();
    }
}
