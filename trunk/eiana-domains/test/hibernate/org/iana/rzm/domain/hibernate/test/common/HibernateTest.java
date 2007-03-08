package org.iana.rzm.domain.hibernate.test.common;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.iana.rzm.domain.dao.DomainDAO;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Jakub Laszkiewicz
 */
abstract public class HibernateTest {
    protected Session session;
    protected Transaction tx = null;
    protected SessionFactory sessionFactory = (SessionFactory) new ClassPathXmlApplicationContext("eiana-domains-spring.xml").getBean("sessionFactory");

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
