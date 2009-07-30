package org.iana.dns.validator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.iana.dns.DNSIPv6Address;
import org.iana.dns.obj.DNSIPv6AddressImpl;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test
public class ReservedIPAddressCheckerTest {

    @Test
    public void testCorrectIPv6() {
        List<DNSIPv6Address> addresses = new ArrayList<DNSIPv6Address>();
        addresses.add(new DNSIPv6AddressImpl("FFFF::"));
        addresses.add(new DNSIPv6AddressImpl("1002:db8::1428:57ab"));
        addresses.add(new DNSIPv6AddressImpl("21DA:D3:0:2F3B:2AA:FF:FE28:9C5A"));
        addresses.add(new DNSIPv6AddressImpl("0::2"));
        addresses.add(new DNSIPv6AddressImpl("1::1"));
        addresses.add(new DNSIPv6AddressImpl("FE00::"));

        for (DNSIPv6Address address : addresses) {
            assert !address.isReserved() : address.getAddress();
        }

    }

    @Test
    public void testIncorrectIPv6() {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        List<DNSIPv6Address> addresses = new ArrayList<DNSIPv6Address>();
        addresses.add(new DNSIPv6AddressImpl("0::0"));
        addresses.add(new DNSIPv6AddressImpl("0::1"));
        addresses.add(new DNSIPv6AddressImpl("2001:2::123"));
        addresses.add(new DNSIPv6AddressImpl("2001:10:23F::"));
        addresses.add(new DNSIPv6AddressImpl("2001:DB8:FFFF::"));
        addresses.add(new DNSIPv6AddressImpl("FC00::"));
        addresses.add(new DNSIPv6AddressImpl("FE80::FFFF"));
        addresses.add(new DNSIPv6AddressImpl("::FFFF:0:0"));
        addresses.add(new DNSIPv6AddressImpl("2001:0:ABC1::"));
        addresses.add(new DNSIPv6AddressImpl("2002:FA00::1"));


        for (DNSIPv6Address address : addresses) {
            assert address.isReserved() : address.getAddress();
        }

    }
}
