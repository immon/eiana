package org.iana.dns.obj;

import org.testng.annotations.Test;
import org.iana.dns.DNSIPv6Address;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test
public class DNSIPv6AddressImplTest {

    @Test
    public void testNormalizationColonFirst() {
        DNSIPv6Address addr = new DNSIPv6AddressImpl("::ff");
        assert "0:0:0:0:0:0:0:ff".equals(addr.getAddress());
    }

    @Test
    public void testNormalizationColonLast() {
        DNSIPv6Address addr = new DNSIPv6AddressImpl("ff::");
        assert "ff:0:0:0:0:0:0:0".equals(addr.getAddress());
    }

    @Test
    public void testNormalizationColonMiddle() {
        DNSIPv6Address addr = new DNSIPv6AddressImpl("ff::1");
        assert "ff:0:0:0:0:0:0:1".equals(addr.getAddress());
    }
}
