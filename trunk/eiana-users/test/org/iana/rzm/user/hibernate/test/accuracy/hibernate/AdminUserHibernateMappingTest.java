package org.iana.rzm.user.hibernate.test.accuracy.hibernate;

import org.iana.rzm.user.AdminUser;
import org.iana.rzm.user.hibernate.test.common.hibernate.HibernateMappingUnitTest;
import org.iana.rzm.user.hibernate.test.common.hibernate.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class AdminUserHibernateMappingTest extends HibernateMappingUnitTest<AdminUser> {
    protected AdminUser create() throws Exception {
        AdminUser adminUser = (AdminUser) HibernateMappingTestUtil.setupUser(new AdminUser(), "created", true);
        adminUser.setType(AdminUser.Type.GOV_OVERSIGHT);
        return adminUser;
    }

    protected AdminUser change(AdminUser o) throws Exception {
        AdminUser adminUser = (AdminUser) HibernateMappingTestUtil.setupUser(o, "changed", false);
        adminUser.setType(AdminUser.Type.IANA_STAFF);
        return adminUser;
    }

    protected Serializable getId(AdminUser o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-user"})
    public void testAdminUser() throws Exception {
        super.test();
    }
}
