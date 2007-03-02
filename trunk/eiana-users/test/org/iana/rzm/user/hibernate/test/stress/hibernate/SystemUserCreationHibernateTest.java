package org.iana.rzm.user.hibernate.test.stress.hibernate;

import org.iana.rzm.user.hibernate.test.common.hibernate.HibernateOperationStressTest;
import org.iana.rzm.user.hibernate.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.user.SystemUser;
import org.iana.rzm.user.Role;
import org.testng.annotations.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Jakub Laszkiewicz
 */
public class SystemUserCreationHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        SystemUser systemUser = (SystemUser) HibernateMappingTestUtil.setupUser(new SystemUser(), "" + o, true);
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new Role(), "1st", true));
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new Role(), "2nd", true));
        session.save(systemUser);
    }

    protected List getList() throws Exception {
        List result = new ArrayList();
        for (int i = 0; i < 1000; i++) result.add("user-" + i);
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
