package org.iana.rzm.user.test.accuracy.hibernate;

import org.iana.rzm.user.SystemUser;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.test.common.hibernate.HibernateMappingTestUtil;
import org.iana.rzm.user.test.common.hibernate.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class SystemUserHibernateMappingTest extends HibernateMappingUnitTest<SystemUser> {
    protected SystemUser create() throws Exception {
        SystemUser systemUser = (SystemUser) HibernateMappingTestUtil.setupUser(new SystemUser(), "created", true);
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new Role(), "1st", true));
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new Role(), "2nd", true));
        return systemUser;
    }

    protected SystemUser change(SystemUser o) throws Exception {
        SystemUser systemUser = (SystemUser) HibernateMappingTestUtil.setupUser(o, "changed", false);
        systemUser.removeRole(systemUser.getRoles().iterator().next());
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new Role(), "3rd", true));
        return systemUser;
    }

    protected Serializable getId(SystemUser o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-user"})
    public void testSystemUser() throws Exception {
        super.test();
    }
}
