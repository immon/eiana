package org.iana.dns.check;

import org.iana.dns.check.exceptions.DuplicatedASNumberException;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"common-dns", "FailureDNSTechnicalCheckTest"})
public class FailureNetworkDiversityCheckTest {

    @Test
    public void doTest() {
        DNSDomainImpl domain = new DNSDomainImpl("de");

        DNSHostImpl host1 = new DNSHostImpl("a.nic.de");
        host1.addIPAddress("80.10.50.50");
        domain.addNameServer(host1);

        DNSHostImpl host2 = new DNSHostImpl("z.nic.de");
        host2.addIPAddress("80.10.50.51");
        domain.addNameServer(host2);

        DNSHostImpl host3 = new DNSHostImpl("f.nic.de");
        host3.addIPAddress("80.10.50.52");
        domain.addNameServer(host3);

        DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();

        List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();
        domainChecks.add(new MinimumNetworkDiversityCheck(new MockWhoIsDataRetriever()));
        dnsTechnicalCheck.setDomainChecks(domainChecks);

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException error = (MultipleDNSTechnicalCheckException) e;
            assert error.getExceptions().size() == 1;
            assert error.getExceptions().iterator().next() instanceof DuplicatedASNumberException;
            DuplicatedASNumberException asError = (DuplicatedASNumberException) error.getExceptions().iterator().next();
            assert "AS1111".equals(asError.getASNumber());
            assert asError.getHosts().size() == 3;
            assert asError.getHosts().containsAll(domain.getNameServers());
        }

    }
}

