package org.iana.rzm.techcheck.failure;

import org.testng.annotations.Test;
import org.iana.rzm.techcheck.exceptions.NotEnoughHostsException;
import org.iana.rzm.techcheck.exceptions.EmptyIPAddressListException;
import org.iana.rzm.techcheck.exceptions.DomainValidationException;
import org.iana.rzm.techcheck.exceptions.DuplicatedIPAddressException;
import org.iana.rzm.techcheck.TechChecker;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;

import java.util.ArrayList;

/**
 * @author: Piotr Tkaczyk
 */

@Test(sequential=true, groups = {"failure", "eiana-techcheck", "FailureTechCheckerTest"})
public class FailureTechCheckerTest {

    Domain domain;
    Host host1, host2;
    @Test (expectedExceptions = {DomainValidationException.class, NotEnoughHostsException.class})
    public void testNotEnoughHosts() throws DomainValidationException {
        domain = new Domain("org");
        TechChecker.checkDomain(domain);
    }

    @Test (expectedExceptions = {DomainValidationException.class, EmptyIPAddressListException.class},
            dependsOnMethods = {"testNotEnoughHosts"})
    public void testEmptyIPAddressList() throws DomainValidationException {
        host1 = new Host("ns1.ultrans.net");
        domain.addNameServer(host1);
        host2 = new Host("ns2.ultrans.net");
        domain.addNameServer(host2);
        TechChecker.checkDomain(domain);
    }

    @Test (expectedExceptions = {DomainValidationException.class, DuplicatedIPAddressException.class},
            dependsOnMethods = {"testEmptyIPAddressList"})
    public void testDuplicatedIPAddress() throws DomainValidationException {
        host1.addIPv4Address("81.50.50.10");
        host2.addIPv4Address("81.50.50.10");
        domain.setNameServers(new ArrayList<Host>());
        domain.addNameServer(host1);
        domain.addNameServer(host2);
        try {
            TechChecker.checkDomain(domain);
        } catch (DuplicatedIPAddressException e) {
            assert e.getIPAddress().equals("81.50.50.10");
            throw e;
        }
    }
}
