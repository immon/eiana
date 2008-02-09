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
            DNSDomainImpl domain = new DNSDomainImpl("int");

            DNSHostImpl host;
            Set<DNSHost> hosts = new HashSet<DNSHost>();
            Set<String> ipAddresses = new HashSet<String>();

            host = new DNSHostImpl("m1.ns.cynosure.com.au");
            ipAddresses.clear();
            ipAddresses.add("64.34.174.8");
            host.setIPAddressesAsStrings(ipAddresses);
            hosts.add(host);

            host = new DNSHostImpl("m2.ns.cynosure.com.au");
            ipAddresses.clear();
            ipAddresses.add("72.51.41.201");
            host.setIPAddressesAsStrings(ipAddresses);
            hosts.add(host);

            host = new DNSHostImpl("trantor.virtualized.org");
            ipAddresses.clear();
            ipAddresses.add("204.152.189.190");
            host.setIPAddressesAsStrings(ipAddresses);
            hosts.add(host);

            domain.setNameServers(hosts);


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
            nsChecks.add(new GlueCoherencyCheck());
            dnsTechnicalCheck.setNameServerChecks(nsChecks);

            dnsTechnicalCheck.check(domain);

        } catch (DNSTechnicalCheckException e) {
            throw e; //for debug
        }
    }
}
