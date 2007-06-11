package org.iana.dns.check;

import org.iana.dns.check.exceptions.ResponseDataSizeExceededException;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"stress", "common-dns", "FailureResponseSizeCheckTest"})
public class FailureMaximumPayloadSizeCheckTest {

    @Test
    public void doTest() {
        DNSDomainImpl domain = new DNSDomainImpl("verylongname");

        DNSHostImpl host1 = new DNSHostImpl("first.very.very.long.nameserver.name");
        host1.addIPAddress("2201:624:253:4405::53");
        host1.addIPAddress("2201:624:253:4405::54");
        host1.addIPAddress("2201:624:253:4405::55");
        host1.addIPAddress("2201:624:253:4405::56");
        host1.addIPAddress("2201:624:253:4405::57");
        host1.addIPAddress("2201:624:253:4405::58");
        host1.addIPAddress("2201:624:253:4405::59");
        host1.addIPAddress("2201:624:253:4405::60");
        host1.addIPAddress("2201:624:253:4405::61");
        domain.addNameServer(host1);

        DNSHostImpl host2 = new DNSHostImpl("second.very.very.long.nameserver.name");
        host2.addIPAddress("2201:624:253:4405::83");
        host2.addIPAddress("2201:624:253:4405::84");
        host2.addIPAddress("2201:624:253:4405::95");
        host2.addIPAddress("2201:624:253:4405::96");
        host2.addIPAddress("2201:624:253:4405::97");
        host2.addIPAddress("2201:624:253:4405::98");
        host2.addIPAddress("2201:624:253:4405::99");
        host2.addIPAddress("2201:624:253:4405::100");
        host2.addIPAddress("2201:624:253:4405::1231");
        domain.addNameServer(host2);

        DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();

        List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();
        domainChecks.add(new MaximumPayloadSizeCheck());
        dnsTechnicalCheck.setDomainChecks(domainChecks);

        try {
            dnsTechnicalCheck.check(domain);
        } catch (DNSTechnicalCheckException e) {
            MultipleDNSTechnicalCheckException error = (MultipleDNSTechnicalCheckException) e;
            assert error.getExceptions().size() == 1;
            assert error.getExceptions().iterator().next() instanceof ResponseDataSizeExceededException;
            ResponseDataSizeExceededException exception = (ResponseDataSizeExceededException) error.getExceptions().iterator().next();
            assert 1192 == exception.getEstimatedSize();
        }
    }
}
