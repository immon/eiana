package org.iana.dns.check;

import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

@Test(sequential = true, groups = {"stress", "common-dns", "RootServersPropagationCheckTest"})
public class RootServersPropagationCheckTest {

    @Test
    public void test() throws DNSTechnicalCheckException {

        try {
            DNSDomainImpl dnsDomain = new DNSDomainImpl("pl");

            DNSHostImpl dnsHost = new DNSHostImpl("a-dns.pl");
            dnsHost.addIPAddress("195.187.245.44");
            dnsDomain.addNameServer(dnsHost);

            dnsHost = new DNSHostImpl("b-dns.pl");
            dnsHost.addIPAddress("80.50.50.10");
            dnsDomain.addNameServer(dnsHost);

            dnsHost = new DNSHostImpl("c-dns.pl");
            dnsHost.addIPAddress("193.171.255.47");
            dnsDomain.addNameServer(dnsHost);

            dnsHost = new DNSHostImpl("d-dns.pl");
            dnsHost.addIPAddress("213.172.174.70");
            dnsDomain.addNameServer(dnsHost);

            dnsHost = new DNSHostImpl("e-dns.pl");
            dnsHost.addIPAddress("213.218.118.26");
            dnsDomain.addNameServer(dnsHost);

            dnsHost = new DNSHostImpl("f-dns.pl");
            dnsHost.addIPAddress("217.17.46.189");
            dnsHost.addIPAddress("2001:1a68:0:10:0:0:0:189");
            dnsDomain.addNameServer(dnsHost);

            dnsHost = new DNSHostImpl("g-dns.pl");
            dnsHost.addIPAddress("149.156.1.6");
            dnsHost.addIPAddress("2001:6d8:0:1:0:0:a:6");
            dnsDomain.addNameServer(dnsHost);

            dnsHost = new DNSHostImpl("h-dns.pl");
            dnsHost.addIPAddress("194.0.1.2");
            dnsDomain.addNameServer(dnsHost);

            DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();

            List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();

            RootServersPropagationCheck check = new RootServersPropagationCheck();
            
            check.setRootServersProducer(new MockRootServersProducer());
            domainChecks.add(check);

            dnsTechnicalCheck.setDomainChecks(domainChecks);
            dnsTechnicalCheck.check(dnsDomain);


        } catch (DNSTechnicalCheckException e) {
            throw e; //for debug
        }
    }
}
