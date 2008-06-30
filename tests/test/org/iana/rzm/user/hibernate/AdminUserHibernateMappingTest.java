package org.iana.rzm.user.hibernate;

import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.Role;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-user"})
public class AdminUserHibernateMappingTest extends HibernateMappingUnitTest<RZMUser> {
    protected RZMUser create() throws Exception {
        RZMUser adminUser = HibernateMappingTestUtil.setupUser(new RZMUser(), "created", true);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        return adminUser;
    }

    protected RZMUser change(RZMUser o) throws Exception {
        RZMUser adminUser = HibernateMappingTestUtil.setupUser(o, "changed", false);
        Role role = adminUser.getRoles().iterator().next();
        adminUser.removeRole(role);
        adminUser.addRole(new AdminRole(AdminRole.AdminType.IANA));
        return adminUser;
    }

    protected Serializable getId(RZMUser o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-user"})
    public void testAdminUser() throws Exception {
        super.test();
    }
}
