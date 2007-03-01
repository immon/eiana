package org.iana.rzm.user.test.stress.hibernate;

import org.iana.rzm.user.SystemUser;
import org.iana.rzm.user.test.common.hibernate.HibernateOperationStressTest;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class SystemUserDeletionHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        session.delete(o);
    }

    protected List getList() throws Exception {
        return session.createCriteria(SystemUser.class).list();
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
