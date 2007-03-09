package org.iana.rzm.user.hibernate.test.common;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.testng.annotations.BeforeClass;

/**
 * @author Jakub Laszkiewicz
 */
abstract public class HibernateTest {
    protected Session session;
    protected Transaction tx = null;
    protected SessionFactory sessionFactory;

    @BeforeClass
    public void init() {
        sessionFactory = new AnnotationConfiguration().configure("eiana-users-hibernate.cfg.xml").buildSessionFactory();
    }

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
}
