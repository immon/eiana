package org.iana.dns.check;

import org.iana.dns.check.exceptions.*;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"common-dns", "FailureDNSTechnicalCheckTest"})
public class FailureDNSTechnicalCheckTest {

    DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();


    @BeforeClass
    public void init() {
        List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();
        domainChecks.add(new NSCountAndIPRestrictions());
        domainChecks.add(new MinimumNetworkDiversity());
        domainChecks.add(new NameServerCoherency());
        domainChecks.add(new SerialNumberCoherency());
        dnsTechnicalCheck.setDomainChecks(domainChecks);

        List<DNSNameServerTechnicalCheck> nsChecks = new ArrayList<DNSNameServerTechnicalCheck>();
        nsChecks.add(new NameServerChecks());
        dnsTechnicalCheck.setNameServerChecks(nsChecks);
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testNotEnoughNamesServers() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException error = (MultipleDNSTechnicalCheckException) e;
            assert error.getExceptions().size() == 1;
            assert error.getExceptions().iterator().next() instanceof NotEnoughNameServersException;
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testUnReachability() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        DNSHostImpl host = new DNSHostImpl("wrong.host.de");
        host.addIPAddress("194.246.96.1");
        host.addIPAddress("2001:628:453:4905::53");
        domain.addNameServer(host);

        host = new DNSHostImpl("a.nic.de");
        host.addIPAddress("193.0.7.3");
        domain.addNameServer(host);

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException error = (MultipleDNSTechnicalCheckException) e;
            assert error.getExceptions().iterator().next().equals(new UnReachableByUDPException("wrong.host.de"));
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testEmptyIPAddressList() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        domain.addNameServer(new DNSHostImpl("z.nic.de"));
        domain.addNameServer(new DNSHostImpl("a.nic.de"));

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException exception = (MultipleDNSTechnicalCheckException) e;
            List<DNSTechnicalCheckException> errors = exception.getExceptions();
            assert errors.contains(new EmptyIPAddressListException("z.nic.de"));
            assert errors.contains(new EmptyIPAddressListException("a.nic.de"));
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testRestrictedAndDuplicatedIPs() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        DNSHostImpl host = new DNSHostImpl("z.nic.de");
        host.addIPAddress("10.0.0.1");
        host.addIPAddress("193.0.7.3");
        domain.addNameServer(host);

        host = new DNSHostImpl("a.nic.de");
        host.addIPAddress("192.168.0.3");
        host.addIPAddress("193.0.7.3");
        domain.addNameServer(host);


        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException exception = (MultipleDNSTechnicalCheckException) e;
            List<DNSTechnicalCheckException> errors = exception.getExceptions();
            assert errors.contains(new RestrictedIPv4Exception("z.nic.de", "10.0.0.1"));
            assert errors.contains(new RestrictedIPv4Exception("a.nic.de", "192.168.0.3"));
            assert errors.contains(new DuplicatedIPAddressException("z.nic.de", "193.0.7.3")) ||
                    errors.contains(new DuplicatedIPAddressException("a.nic.de", "193.0.7.3"));
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testNameServerCoherency() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        DNSHostImpl host = new DNSHostImpl("z.nic.de");
        host.addIPAddress("194.246.96.1");
        host.addIPAddress("2001:628:453:4905::53");
        domain.addNameServer(host);

        host = new DNSHostImpl("a.nic.de");
        host.addIPAddress("193.0.7.3");
        domain.addNameServer(host);

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException exception = (MultipleDNSTechnicalCheckException) e;
            List<DNSTechnicalCheckException> errors = exception.getExceptions();
            assert errors.contains(new NameServerCoherencyException());
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testNotEqualsNameServerIPs() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        DNSHostImpl host = new DNSHostImpl("z.nic.de");
        host.addIPAddress("194.246.96.1");
        host.addIPAddress("2001:628:453:4905::53");
        domain.addNameServer(host);

        host = new DNSHostImpl("a.nic.de");
        host.addIPAddress("193.0.7.3");
        domain.addNameServer(host);

        host = new DNSHostImpl("f.nic.de");
        host.addIPAddress("81.91.164.5");
        domain.addNameServer(host);

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException exception = (MultipleDNSTechnicalCheckException) e;
            List<DNSTechnicalCheckException> errors = exception.getExceptions();
            assert errors.contains(new NameServerIPAddressesNotEqualException("f.nic.de"));
            throw e;
        }
    }
}
