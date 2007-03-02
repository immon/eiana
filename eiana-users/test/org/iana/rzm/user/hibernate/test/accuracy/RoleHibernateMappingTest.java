package org.iana.rzm.user.hibernate.test.accuracy;

import org.iana.rzm.user.Role;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingUnitTest;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class RoleHibernateMappingTest extends HibernateMappingUnitTest<Role> {
    protected Role create() throws Exception {
        return HibernateMappingTestUtil.setupRole(new Role(), "created", true);
    }

    protected Role change(Role o) throws Exception {
        Role changed = HibernateMappingTestUtil.setupRole(o, "changed", false);
        changed.setType(Role.Type.SO);
        return changed;
    }

    protected Serializable getId(Role o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-user"})
    public void testRole() throws Exception {
        super.test();
    }
}
