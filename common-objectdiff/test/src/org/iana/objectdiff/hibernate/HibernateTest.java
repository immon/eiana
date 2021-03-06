package org.iana.objectdiff.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "common-objectdiff"})
abstract public class HibernateTest {
    protected Session session;
    protected Transaction tx = null;
    protected SessionFactory sessionFactory;

    @BeforeClass
    public void init() {
        synchronized (HibernateTest.class) {
            if (sessionFactory == null) sessionFactory = new AnnotationConfiguration().configure("common-objectdiff-hibernate.cfg.xml").buildSessionFactory();
        }
    }

    @AfterClass (alwaysRun = true)
    public void destroy() {
        if (tx != null) {
            try {
                tx.rollback();
            } catch (Exception e) {
            }
        }
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
            }
        }
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

    protected void rollbackTx() {
        tx.rollback();
        tx = null;
    }
    
    protected void closeTx() {
        tx.commit();
        tx = null;
    }
}
