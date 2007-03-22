package org.iana.rzm.user.hibernate.test.accuracy;

import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingTestUtil;
import org.iana.rzm.user.hibernate.test.common.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class RoleHibernateMappingTest extends HibernateMappingUnitTest<SystemRole> {
    protected SystemRole create() throws Exception {
        return HibernateMappingTestUtil.setupRole(new SystemRole(), "created", true);
    }

    protected SystemRole change(SystemRole o) throws Exception {
        SystemRole changed = HibernateMappingTestUtil.setupRole(o, "changed", false);
        changed.setType(SystemRole.SystemType.SO);
        return changed;
    }

    protected Serializable getId(SystemRole o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-user"})
    public void testRole() throws Exception {
        super.test();
    }
}
