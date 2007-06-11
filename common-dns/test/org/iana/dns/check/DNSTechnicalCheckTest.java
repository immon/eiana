package org.iana.dns.check;

import org.iana.dns.DNSHost;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"stress", "common-dns", "DNSTechnicalCheckTest"})
public class DNSTechnicalCheckTest {

    @Test
    public void testDNSTechnicalCheck4DE() throws DNSTechnicalCheckException {

        try {
            DNSDomainImpl domain = new DNSDomainImpl("de");

            DNSHostImpl host;
            Set<DNSHost> hosts = new HashSet<DNSHost>();
            Set<String> ipAddresses = new HashSet<String>();

            host = new DNSHostImpl("z.nic.de");
            ipAddresses.clear();
            ipAddresses.add("194.246.96.1");
            ipAddresses.add("2001:628:453:4905::53");
            host.setIPAddressesAsStrings(ipAddresses);
            hosts.add(host);

            host = new DNSHostImpl("a.nic.de");
            ipAddresses.clear();
            ipAddresses.add("193.0.7.3");
            host.setIPAddressesAsStrings(ipAddresses);
            hosts.add(host);

            domain.setNameServers(hosts);

            host = new DNSHostImpl("f.nic.de");
            ipAddresses.clear();
            ipAddresses.add("81.91.164.5");
            ipAddresses.add("2001:608:6::5");
            host.setIPAddressesAsStrings(ipAddresses);
            domain.addNameServer(host);

            host = new DNSHostImpl("c.de.net");
            host.addIPAddress("208.48.81.43");
            domain.addNameServer(host);

            host = new DNSHostImpl("l.de.net");
            host.addIPAddress("217.51.137.213");
            domain.addNameServer(host);

            host = new DNSHostImpl("s.de.net");
            host.addIPAddress("193.159.170.149");
            domain.addNameServer(host);


            DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();

            List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();
            domainChecks.add(new MinimumNameServersAndNoReservedIPsCheck());
            domainChecks.add(new MinimumNetworkDiversityCheck());
            domainChecks.add(new NameServerCoherencyCheck());
            domainChecks.add(new SerialNumberCoherencyCheck());
            domainChecks.add(new MaximumPayloadSizeCheck());
            dnsTechnicalCheck.setDomainChecks(domainChecks);

            List<DNSNameServerTechnicalCheck> nsChecks = new ArrayList<DNSNameServerTechnicalCheck>();
            nsChecks.add(new NameServerReachabilityCheck());
            nsChecks.add(new NameServerAuthorityCheck());
//            nsChecks.add(new GlueCoherencyCheck());
            dnsTechnicalCheck.setNameServerChecks(nsChecks);

            dnsTechnicalCheck.check(domain);

        } catch (DNSTechnicalCheckException e) {
            throw e; //for debug
        }
    }
}
