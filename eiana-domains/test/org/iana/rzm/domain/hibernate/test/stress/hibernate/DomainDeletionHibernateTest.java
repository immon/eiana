package org.iana.rzm.domain.hibernate.test.stress.hibernate;

import org.iana.rzm.domain.hibernate.test.common.hibernate.HibernateOperationStressTest;
import org.iana.rzm.domain.Domain;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class DomainDeletionHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        session.delete(o);
    }

    protected List getList() throws Exception {
        return session.createCriteria(Domain.class).list();
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
