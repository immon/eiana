package org.iana.rzm.user.hibernate.test.stress;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.hibernate.test.common.HibernateOperationStressTest;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-users", "stress", "eiana-users-stress-deletion"})
public class SystemUserDeletionHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        session.delete(o);
    }

    protected List getList() throws Exception {
        return session.createCriteria(RZMUser.class).list();
    }

    @Test(dependsOnGroups = "eiana-users-stress-update")
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
