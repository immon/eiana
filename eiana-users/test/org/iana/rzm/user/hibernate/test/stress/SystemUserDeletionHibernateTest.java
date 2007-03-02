package org.iana.rzm.user.hibernate.test.stress;

import org.iana.rzm.user.SystemUser;
import org.iana.rzm.user.hibernate.test.common.HibernateOperationStressTest;
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
