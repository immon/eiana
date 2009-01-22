package org.iana.rzm.domain.hibernate;

import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.rzm.domain.IPAddress;
import org.testng.annotations.Test;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"hibernate", "eiana-domains"})
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

    @Test
    public void testIPAddress() throws Exception {
        super.test();
    }
}
