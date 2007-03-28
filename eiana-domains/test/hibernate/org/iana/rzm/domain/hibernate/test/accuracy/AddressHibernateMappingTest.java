package org.iana.rzm.domain.hibernate.test.accuracy;

import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingUnitTest;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains"})
public class AddressHibernateMappingTest extends HibernateMappingUnitTest<Address> {
    protected Address create() {
        return HibernateMappingTestUtil.setupAddress(new Address(), "created");
    }

    protected Address change(Address o) {
        return HibernateMappingTestUtil.setupAddress(o, "changed");
    }

    protected Serializable getId(Address o) {
        return o.getObjId();
    }

    @Test
    public void testAddress() throws Exception {
        super.test();
    }
}
