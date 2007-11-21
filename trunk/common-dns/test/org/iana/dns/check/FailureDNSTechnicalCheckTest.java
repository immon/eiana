package org.iana.dns.check;

import org.iana.dns.check.exceptions.*;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"common-dns", "FailureDNSTechnicalCheckTest"})
public class FailureDNSTechnicalCheckTest {

    DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();


    @BeforeClass
    public void init() {
        List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();
        domainChecks.add(new MinimumNameServersAndNoReservedIPsCheck());
        domainChecks.add(new MinimumNetworkDiversityCheck());
        domainChecks.add(new NameServerCoherencyCheck());
        domainChecks.add(new SerialNumberCoherencyCheck());
        dnsTechnicalCheck.setDomainChecks(domainChecks);

        List<DNSNameServerTechnicalCheck> nsChecks = new ArrayList<DNSNameServerTechnicalCheck>();
        nsChecks.add(new NameServerReachabilityCheck());
        nsChecks.add(new NameServerAuthorityCheck());
        nsChecks.add(new GlueCoherencyCheck());
//        sizeCheck
        dnsTechnicalCheck.setNameServerChecks(nsChecks);
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testNotEnoughNamesServers() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException error = (MultipleDNSTechnicalCheckException) e;
            assert error.getExceptions().size() == 2;
            assert error.getExceptions().iterator().next() instanceof NotEnoughNameServersException;
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testUnReachability() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        DNSHostImpl host1 = new DNSHostImpl("wrong.host.de");
        host1.addIPAddress("194.246.96.1");
        host1.addIPAddress("2001:628:453:4905::53");
        domain.addNameServer(host1);

        DNSHostImpl host2 = new DNSHostImpl("a.nic.de");
        host2.addIPAddress("193.0.7.3");
        domain.addNameServer(host2);


        dnsTechnicalCheck.check(domain);
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
            assert errors.contains(new EmptyIPAddressListException(domain, new DNSHostImpl("z.nic.de")));
            assert errors.contains(new EmptyIPAddressListException(domain, new DNSHostImpl("a.nic.de")));
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testRestrictedAndDuplicatedIPs() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        DNSHostImpl host1 = new DNSHostImpl("z.nic.de");
        host1.addIPAddress("10.0.0.1");
        host1.addIPAddress("193.0.7.3");
        domain.addNameServer(host1);

        DNSHostImpl host2 = new DNSHostImpl("a.nic.de");
        host2.addIPAddress("192.168.0.3");
        host2.addIPAddress("193.0.7.3");
        domain.addNameServer(host2);


        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException exception = (MultipleDNSTechnicalCheckException) e;
            List<DNSTechnicalCheckException> errors = exception.getExceptions();
            assert errors.contains(new ReservedIPv4Exception(domain, host1, DNSIPAddressImpl.createIPAddress("10.0.0.1")));
            assert errors.contains(new ReservedIPv4Exception(domain, host2, DNSIPAddressImpl.createIPAddress("192.168.0.3")));
            assert errors.contains(new DuplicatedIPAddressException(domain, host1, DNSIPAddressImpl.createIPAddress("193.0.7.3"))) ||
                    errors.contains(new DuplicatedIPAddressException(domain, host2, DNSIPAddressImpl.createIPAddress("193.0.7.3")));
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
            assert errors.contains(new NameServerCoherencyException(domain));
            throw e;
        }
    }

    @Test(expectedExceptions = MultipleDNSTechnicalCheckException.class)
    public void testNotEqualsNameServerIPs() throws DNSTechnicalCheckException {
        DNSDomainImpl domain = new DNSDomainImpl("de");
        DNSHostImpl host1 = new DNSHostImpl("z.nic.de");
        host1.addIPAddress("194.246.96.1");
        host1.addIPAddress("2001:628:453:4905::53");
        domain.addNameServer(host1);

        DNSHostImpl host2 = new DNSHostImpl("a.nic.de");
        host2.addIPAddress("193.0.7.3");
        domain.addNameServer(host2);

        DNSHostImpl host3 = new DNSHostImpl("f.nic.de");
        host3.addIPAddress("81.91.164.5");
        domain.addNameServer(host3);

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException exception = (MultipleDNSTechnicalCheckException) e;
            List<DNSTechnicalCheckException> errors = exception.getExceptions();
            assert errors.contains(new NameServerIPAddressesNotEqualException(host3));
            throw e;
        }
    }
}
