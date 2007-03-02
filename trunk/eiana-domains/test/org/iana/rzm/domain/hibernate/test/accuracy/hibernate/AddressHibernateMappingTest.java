package org.iana.rzm.domain.hibernate.test.accuracy.hibernate;

import org.iana.rzm.domain.Address;
import org.iana.rzm.domain.hibernate.test.common.hibernate.HibernateMappingUnitTest;
import org.iana.rzm.domain.hibernate.test.common.hibernate.HibernateMappingTestUtil;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
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

    @Test(groups = {"hibernate", "eiana-domains"})
    public void testAddress() throws Exception {
        super.test();
    }
}
