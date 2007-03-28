package org.iana.rzm.domain.hibernate.test.stress;

import org.iana.rzm.domain.hibernate.test.common.HibernateOperationStressTest;
import org.iana.rzm.domain.Domain;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains","stress"})
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
