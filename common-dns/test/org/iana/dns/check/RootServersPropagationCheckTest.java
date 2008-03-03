package org.iana.dns.check;

import org.testng.annotations.Test;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
            Map<String, String> rootServers = new HashMap<String, String>();
            rootServers.put("a.root-servers.net", "198.41.0.4");
            rootServers.put("b.root-servers.net", "192.228.79.201");
            rootServers.put("c.root-servers.net", "192.33.4.12");
            rootServers.put("d.root-servers.net", "128.8.10.90");
            rootServers.put("e.root-servers.net", "192.203.230.10");
            rootServers.put("f.root-servers.net", "192.5.5.241");
            rootServers.put("g.root-servers.net", "192.112.36.4");
            rootServers.put("h.root-servers.net", "128.63.2.53");
            rootServers.put("i.root-servers.net", "192.36.148.17");
            rootServers.put("j.root-servers.net", "192.58.128.30");
            rootServers.put("k.root-servers.net", "193.0.14.129");
            rootServers.put("l.root-servers.net", "199.7.83.42");
            rootServers.put("m.root-servers.net", "202.12.27.33");
            check.setRootServers(rootServers);
            domainChecks.add(check);

            dnsTechnicalCheck.setDomainChecks(domainChecks);
            dnsTechnicalCheck.check(dnsDomain);


        } catch (DNSTechnicalCheckException e) {
            throw e; //for debug
        }
    }
}
