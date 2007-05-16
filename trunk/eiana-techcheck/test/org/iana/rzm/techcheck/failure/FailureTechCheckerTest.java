package org.iana.rzm.techcheck.failure;

import org.testng.annotations.Test;
import org.iana.rzm.techcheck.exceptions.*;
import org.iana.rzm.techcheck.TechChecker;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"failure", "eiana-techcheck", "FailureTechCheckerTest"})
public class FailureTechCheckerTest {

    Domain domain;
    Host host1, host2;

    @Test
    public void testNotEnoughHosts() {
        try {
            domain = new Domain("org");
            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            List<ExceptionMessage> errors = e.getErrorsByExceptionType(NotEnoughHostsException.class.getSimpleName());
            assert errors.size() == 1;
            assert errors.iterator().next().getOwner().equals("org");
        }
    }

    @Test (dependsOnMethods = {"testNotEnoughHosts"})
    public void testEmptyIPAddressList() {
        try {
            domain = new Domain("org");
            host1 = new Host("tld1.ultradns.net");
            domain.addNameServer(host1);
            host2 = new Host("tld2.ultradns.net");
            domain.addNameServer(host2);

            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            List<ExceptionMessage> errors = e.getErrorsByExceptionType(EmptyIPAddressListException.class.getSimpleName());
            assert !errors.isEmpty() && errors.size() == 2;

            List<String> hostNames = new ArrayList<String>();
            for (Host host : domain.getNameServers())
                hostNames.add(host.getName());

            List<String> errorHost = new ArrayList<String>();
            for (ExceptionMessage errorMessage : errors)
                errorHost.add(errorMessage.getOwner());

            assert hostNames.equals(errorHost);
        }
    }

    @Test (dependsOnMethods = {"testEmptyIPAddressList"})
    public void testWrongHostName() {
        domain = new Domain("org");
        host1 = new Host("tld1.ultradns.net");
        host1.addIPv4Address("204.74.112.1");
        domain.addNameServer(host1);
        host2 = new Host("tld2");
        host2.addIPv4Address("204.74.113.1");
        domain.addNameServer(host2);
        try {
            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            List<ExceptionMessage> errors = e.getErrorsByExceptionType(UnknownHostException.class.getSimpleName());
            assert !errors.isEmpty() && errors.size() == 1;
            assert errors.iterator().next().getOwner().equals("tld2");
        }
    }

    @Test (dependsOnMethods = {"testWrongHostName"})
    public void testRestrictedIPAddress() {
        domain = new Domain("org");
        host1 = new Host("tld1.ultradns.net");
        host1.addIPv4Address("204.74.112.1");
        domain.addNameServer(host1);
        host2 = new Host("tld2.ultradns.net");
        host2.addIPv4Address("10.0.0.1");
        domain.addNameServer(host2);
        try {
            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            List<ExceptionMessage> errors = e.getErrorsByExceptionType(RestrictedIPv4Exception.class.getSimpleName());
            assert !errors.isEmpty() && errors.size() == 1;
            assert errors.iterator().next().getOwner().equals("tld2.ultradns.net");
        }
    }

    @Test (dependsOnMethods = {"testRestrictedIPAddress"})
    public void testDuplicatedIPAddress() {
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
        } catch (DomainCheckException e) {
            List<ExceptionMessage> errors = e.getErrorsByExceptionType(DuplicatedIPAddressException.class.getSimpleName());
            assert !errors.isEmpty() && errors.size() == 1;
            assert errors.iterator().next().getOwner().equals("tld2.ultradns.net");

            Map<String, String> error = e.getOwnerExceptions("tld2.ultradns.net");
            assert !error.isEmpty() && error.containsKey(DuplicatedIPAddressException.class.getSimpleName());
            assert error.get(DuplicatedIPAddressException.class.getSimpleName()).equals("204.74.112.1");
        }
    }

    @Test (dependsOnMethods = {"testDuplicatedIPAddress"})
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
        } catch (DomainCheckException e) {
            List<ExceptionMessage> errors = e.getErrorsByExceptionType(NoAuthoritativeNameServerException.class.getSimpleName());
            assert !errors.isEmpty() && errors.size() == 1;
            assert errors.iterator().next().getOwner().equals("a.gtld-servers.net");
        }
    }

    @Test ()
    public void testHostIPSet() {
        try {
            domain = new Domain("org");
            host1 = new Host("tld3.ultradns.org"); //good
            host1.addIPAddress("199.7.66.1");
            domain.addNameServer(host1);
            host2 = new Host("b0.org.afilias-nst.org");
            host2.addIPAddress("2001:500:c::1");
            domain.addNameServer(host2);

            TechChecker.checkDomain(domain);
        } catch (DomainCheckException e) {
            List<ExceptionMessage> errors = e.getErrorsByExceptionType(HostIPSetNotEqualException.class.getSimpleName());
            assert !errors.isEmpty() && errors.size() == 1;
            assert errors.iterator().next().getOwner().equals("b0.org.afilias-nst.org");
        }
    }
}
