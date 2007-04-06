package org.iana.rzm.user.hibernate.test.stress;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.user.hibernate.test.common.HibernateOperationStressTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"eiana-users", "stress", "eiana-users-stress-creation"})
public class SystemUserCreationHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        RZMUser systemUser = HibernateMappingTestUtil.setupUser(new RZMUser(), "" + o, true);
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "1st", true));
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "2nd", true));
        session.save(systemUser);
    }

    protected List getList() throws Exception {
        List result = new ArrayList();
        for (int i = 0; i < 100; i++) result.add("user-" + i);
        return result;
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }

    @Test
    public void manyTransactions() throws Exception {
        super.manyTransactions();
    }

    @Test
    public void manySessions() throws Exception {
        super.manySessions();
    }
}
