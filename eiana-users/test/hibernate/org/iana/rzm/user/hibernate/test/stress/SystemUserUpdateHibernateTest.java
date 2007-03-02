package org.iana.rzm.user.hibernate.test.stress;

import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemUser;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.user.hibernate.test.common.HibernateOperationStressTest;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class SystemUserUpdateHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        SystemUser systemUser = (SystemUser) HibernateMappingTestUtil.setupUser((SystemUser ) o, "changed", false);
        systemUser.removeRole(systemUser.getRoles().iterator().next());
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new Role(), "3rd", true));
        session.save(systemUser);
    }

    protected List getList() throws Exception {
        return session.createCriteria(SystemUser.class).list();
    }

    @Test
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
