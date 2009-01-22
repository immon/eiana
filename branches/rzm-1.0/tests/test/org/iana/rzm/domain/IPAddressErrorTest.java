package org.iana.rzm.domain;

import org.iana.dns.validator.InvalidIPv4AddressException;
import org.iana.dns.validator.InvalidIPv6AddressException;
import org.testng.annotations.Test;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"ipAddressTest", "eiana-domains"})
public class IPAddressErrorTest {

    @Test (expectedExceptions = {InvalidIPv4AddressException.class})
    public void testWrongIPv4Address1() {
        String ip = "68.56.23.345";
        try {
            IPAddress.createIPv4Address(ip);
        } catch (InvalidIPv4AddressException e) {
            assert e.getAddress().equals(ip);
            throw e;
        }
    }

    @Test (expectedExceptions = {InvalidIPv4AddressException.class})
    public void testWrongIPv4Address2() {
        String ip = "68.56.23.A";
        try {
            IPAddress.createIPv4Address(ip);
        } catch (InvalidIPv4AddressException e) {
            assert e.getAddress().equals(ip);
            throw e;
        }
    }

    @Test (expectedExceptions = {InvalidIPv6AddressException.class})
    public void testWrongIPv6Address1() {
        String ip = "FFFF::0::F3:68.56.23.34";
        try {
            IPAddress.createIPv6Address(ip);
        } catch (InvalidIPv6AddressException e) {
            assert e.getAddress().equals(ip);
            throw e;
        }
    }

    @Test (expectedExceptions = {InvalidIPv6AddressException.class})
    public void testWrongIPv6Address2() {
        String ip = "F:1:40:2:F3:23:68.526.23.34";
        try {
            IPAddress.createIPv6Address(ip);
        } catch (InvalidIPv6AddressException e) {
            assert e.getAddress().equals(ip);
            throw e;
        }
    }

    @Test (expectedExceptions = {InvalidIPv6AddressException.class})
    public void testWrongIPv6Address3() {
        String ip = "F:1:40:2::23:68.526.23.34";
        try {
            IPAddress.createIPv6Address(ip);
        } catch (InvalidIPv6AddressException e) {
            assert e.getAddress().equals(ip);
            throw e;
        }
    }

    @Test (expectedExceptions = {InvalidIPv6AddressException.class})
    public void testWrongIPv6Address4() {
        String ip = "F:1:40:::2:2";
        try {
            IPAddress.createIPv6Address(ip);
        } catch (InvalidIPv6AddressException e) {
            assert e.getAddress().equals(ip);
            throw e;
        }
    }

    @Test (expectedExceptions = {InvalidIPv6AddressException.class})
    public void testWrongIPv6Address5() {
        String ip = "F:1:4G:2:1:23:3:F";
        try {
            IPAddress.createIPv6Address(ip);
        } catch (InvalidIPv6AddressException e) {
            assert e.getAddress().equals(ip);
            throw e;
        }
    }
}
