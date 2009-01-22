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
        assert "0:0:0:0:0:0:0:FF".equals(addr.getAddress());
    }

    @Test
    public void testNormalizationColonLast() {
        DNSIPv6Address addr = new DNSIPv6AddressImpl("ff::");
        assert "FF:0:0:0:0:0:0:0".equals(addr.getAddress());
    }

    @Test
    public void testNormalizationColonMiddle1() {
        DNSIPv6Address addr = new DNSIPv6AddressImpl("ff::1");
        assert "FF:0:0:0:0:0:0:1".equals(addr.getAddress());
    }

    @Test
    public void testNormalizationColonMiddle2() {
        DNSIPv6Address addr = new DNSIPv6AddressImpl("ff::2:1");
        assert "FF:0:0:0:0:0:2:1".equals(addr.getAddress());
    }
}
