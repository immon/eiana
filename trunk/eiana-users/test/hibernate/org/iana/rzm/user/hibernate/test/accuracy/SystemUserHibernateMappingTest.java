package org.iana.rzm.user.hibernate.test.accuracy;

import org.iana.rzm.user.Role;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class SystemUserHibernateMappingTest extends HibernateMappingUnitTest<RZMUser> {
    protected RZMUser create() throws Exception {
        RZMUser systemUser = HibernateMappingTestUtil.setupUser(new RZMUser(), "created", true);
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "1st", true));
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "2nd", true));
        return systemUser;
    }

    protected RZMUser change(RZMUser o) throws Exception {
        RZMUser systemUser = HibernateMappingTestUtil.setupUser(o, "changed", false);
        systemUser.removeRole(systemUser.getRoles().iterator().next());
        systemUser.addRole(HibernateMappingTestUtil.setupRole(new SystemRole(), "3rd", true));
        return systemUser;
    }

    protected Serializable getId(RZMUser o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-user"})
    public void testSystemUser() throws Exception {
        super.test();
    }
}
