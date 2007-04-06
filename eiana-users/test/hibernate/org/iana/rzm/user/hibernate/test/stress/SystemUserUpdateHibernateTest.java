package org.iana.rzm.user.hibernate.test.stress;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.user.hibernate.test.common.HibernateOperationStressTest;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-users", "stress", "eiana-users-stress-update"},
        dependsOnGroups = {"eiana-users-stress-creation"})
public class SystemUserUpdateHibernateTest extends HibernateOperationStressTest {
    protected void operation(Object o) throws Exception {
        RZMUser systemUser = HibernateMappingTestUtil.setupUser((RZMUser ) o, "changed", false);
        systemUser.removeRole(systemUser.getRoles().iterator().next());
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "3rd", true));
        session.save(systemUser);
    }

    protected List getList() throws Exception {
        return session.createCriteria(RZMUser.class).list();
    }

    @Test(dependsOnGroups = "eiana-users-stress-creation")
    public void oneTransaction() throws Exception {
        super.oneTransaction();
    }
}
