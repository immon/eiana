package org.iana.rzm.domain.hibernate.test.accuracy;

import org.iana.rzm.domain.IPAddress;
import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.domain.hibernate.test.common.HibernateMappingUnitTest;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class IPAddressHibernateMappingTest extends HibernateMappingUnitTest<IPAddress> {
    protected IPAddress create() throws InvalidIPAddressException {
        return IPAddress.createIPv4Address("1.2.3.4");
    }

    protected IPAddress change(IPAddress o) {
        return o;
    }

    protected Serializable getId(IPAddress o) {
        return o.getObjId();
    }

    @Test(groups = {"hibernate", "eiana-domains"})
    public void testIPAddress() throws Exception {
        super.test();
    }
}
