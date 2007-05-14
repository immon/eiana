package org.iana.rzm.techcheck.failure;

import org.testng.annotations.Test;
import org.iana.rzm.techcheck.exceptions.*;
import org.iana.rzm.techcheck.TechChecker;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"failure", "eiana-techcheck", "FailureTechCheckerTest"})
public class FailureTechCheckerTest {

    Domain domain;
    Host host1, host2;

    @Test (expectedExceptions = {NotEnoughHostsException.class})
    public void testNotEnoughHosts() throws DomainCheckException {
        domain = new Domain("org");
        TechChecker.checkDomain(domain);
    }

    @Test (expectedExceptions = {EmptyIPAddressListException.class},
            dependsOnMethods = {"testNotEnoughHosts"})
    public void testEmptyIPAddressList() throws DomainCheckException {
        host1 = new Host("ns1.ultrans.net");
        domain.addNameServer(host1);
        host2 = new Host("ns2.ultrans.net");
        domain.addNameServer(host2);
        TechChecker.checkDomain(domain);
    }

    @Test (expectedExceptions = {UnknownNameServerException.class},
            dependsOnMethods = {"testEmptyIPAddressList"})
    public void testWrongHostName() throws DomainCheckException {
        domain = new Domain("org");
        host1 = new Host("tld1.ultradns.net");
        host1.addIPv4Address("204.74.112.1");
        domain.addNameServer(host1);
        host2 = new Host("tld2");
        host2.addIPv4Address("204.74.113.1");
        domain.addNameServer(host2);
        try {
            TechChecker.checkDomain(domain);
        } catch (UnknownNameServerException e) {
            assert e.getNameServerName().equals("tld2");
            throw e;
        }
    }

    @Test (expectedExceptions = {DuplicatedIPAddressException.class},
            dependsOnMethods = {"testWrongHostName"})
    public void testDuplicatedIPAddress() throws DomainCheckException {
        domain = new Domain("org");
        host1 = new Host("tld1.ultradns.net");
        host1.addIPv4Address("204.74.112.1");
        domain.addNameServer(host1);
        host2 = new Host("tld2.ultradns.net");
        host2.addIPv4Address("204.74.113.1");
        host2.addIPv4Address("204.74.112.1");
        domain.addNameServer(host2);
        try {
            TechChecker.checkDomain(domain);
        } catch (DuplicatedIPAddressException e) {
            assert e.getIPAddress().equals("204.74.112.1");
            throw e;
        }
    }

    @Test (expectedExceptions = {NoAuthoritativeNameServerException.class},
            dependsOnMethods = {"testDuplicatedIPAddress"})
    public void testNoAuthoritativeNS() throws DomainCheckException {

        domain = new Domain("org");
        host1 = new Host("tld3.ultradns.org"); //good
        host1.addIPAddress("199.7.66.1");
        domain.addNameServer(host1);
        host2 = new Host("a.gtld-servers.net"); //wrong
        host2.addIPAddress("192.5.6.30");
        domain.addNameServer(host2);

        try {
            TechChecker.checkDomain(domain);
        } catch (NoAuthoritativeNameServerException e) {
            assert e.getNameServerName().equals("a.gtld-servers.net");
            throw e;
        }
    }

    @Test (expectedExceptions = {HostIPSetNotEqualException.class})
    public void testHostIPSet() throws DomainCheckException {

        domain = new Domain("org");
        host1 = new Host("tld3.ultradns.org"); //good
        host1.addIPAddress("199.7.66.1");
        domain.addNameServer(host1);
        host2 = new Host("b0.org.afilias-nst.org");
        host2.addIPAddress("2001:500:c::1");
        domain.addNameServer(host2);

        TechChecker.checkDomain(domain);
    }
}
