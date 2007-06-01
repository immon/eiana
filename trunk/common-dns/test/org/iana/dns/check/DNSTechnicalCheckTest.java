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
 * @author: Piotr Tkaczyk
 */
@Test(sequential = true, groups = {"common-dns", "DNSTechnicalCheckTest"})
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

            DNSTechnicalCheck dnsTechnicalCheck = new DNSTechnicalCheck();

            List<DNSDomainTechnicalCheck> domainChecks = new ArrayList<DNSDomainTechnicalCheck>();
            domainChecks.add(new DomainNameServersCheck());
            dnsTechnicalCheck.setDomainChecks(domainChecks);

            List<DNSNameServerTechnicalCheck> nsChecks = new ArrayList<DNSNameServerTechnicalCheck>();
            nsChecks.add(new NameServerChecks());
            dnsTechnicalCheck.setNameServerChecks(nsChecks);

            dnsTechnicalCheck.check(domain);

        } catch (DNSTechnicalCheckException e) {
            throw e; //for debug
        }
    }
}
