package org.iana.rzm.user.hibernate.test.common;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.iana.rzm.user.conf.SpringUsersApplicationContext;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-user"})
abstract public class HibernateTest {
    protected Session session;
    protected Transaction tx = null;
    protected SessionFactory sessionFactory;

    @BeforeClass
    public void init() {
        sessionFactory = (SessionFactory) SpringUsersApplicationContext.getInstance().getContext().getBean("sessionFactory");
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
