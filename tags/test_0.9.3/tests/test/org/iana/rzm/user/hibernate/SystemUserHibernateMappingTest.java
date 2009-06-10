package org.iana.rzm.user.hibernate;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-user"})
public class SystemUserHibernateMappingTest extends HibernateMappingUnitTest<RZMUser> {
    protected RZMUser create() throws Exception {
        RZMUser systemUser = HibernateMappingTestUtil.setupUser(new RZMUser(), "created", true);
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "st1.org", true));
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "nd2.org", true));
        return systemUser;
    }

    protected RZMUser change(RZMUser o) throws Exception {
        RZMUser systemUser = HibernateMappingTestUtil.setupUser(o, "changed", false);
        systemUser.removeRole(systemUser.getRoles().iterator().next());
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "rd3.org", true));
        return systemUser;
    }

    protected Serializable getId(RZMUser o) {
        return o.getObjId();
    }

    @Test
    public void testSystemUser() throws Exception {
        super.test();
    }
}
