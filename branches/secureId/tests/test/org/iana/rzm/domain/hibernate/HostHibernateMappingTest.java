package org.iana.rzm.domain.hibernate;

import org.iana.rzm.domain.Host;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains"})
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

    @Test
    public void testHost() throws Exception {
        super.test();
    }
}
