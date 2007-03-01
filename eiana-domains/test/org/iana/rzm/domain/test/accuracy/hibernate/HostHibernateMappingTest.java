package org.iana.rzm.domain.test.accuracy.hibernate;

import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.test.common.hibernate.HibernateMappingUnitTest;
import org.iana.rzm.domain.test.common.hibernate.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class HostHibernateMappingTest extends HibernateMappingUnitTest<Host> {
    protected Host create() throws Exception {
        return HibernateMappingTestUtil.setupHost(new Host("host.iana.org"));
    }

    protected Host change(Host o) throws Exception {
        return HibernateMappingTestUtil.setupHost(o, "changed");
    }

    protected Serializable getId(Host o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-domains"})
    public void testHost() throws Exception {
        super.test();
    }
}
