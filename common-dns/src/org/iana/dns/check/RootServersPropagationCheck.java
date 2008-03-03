package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.exceptions.RootServersPropagationException;
import org.iana.dns.obj.DNSHostImpl;
import org.xbill.DNS.Record;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.AAAARecord;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
public class RootServersPropagationCheck implements DNSDomainTechnicalCheck {

    Set<DNSHost> rootServers = new HashSet<DNSHost>();

    public void setRootServers(Map<String, String> rootServers) {
        for(String name : rootServers.keySet()) {
            DNSHostImpl dnsHost = new DNSHostImpl(name);
            dnsHost.addIPAddress(rootServers.get(name));
            this.rootServers.add(dnsHost);
        }
    }

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {

        for( DNSHost dnsHost : rootServers) {
            DNSNameServer nameServer = new DNSNameServer(domain, dnsHost);
            Set<DNSHost> retRecords = convertToDNSHosts(nameServer.getAdditionalSection());

            Set<DNSHost> domainNameServers = domain.getNameServers();
            if (!domainNameServers.equals(retRecords)) {
                throw new RootServersPropagationException(domain, dnsHost);
            }
        }
    }

    private Set<DNSHost> convertToDNSHosts(List<Record> records) {
        Map<String, DNSHostImpl> retHosts = new HashMap<String, DNSHostImpl>();
        for (Record record : records) {
            String address;
            if (ARecord.class.isAssignableFrom(record.getClass()))
                address = ((ARecord)record).getAddress().getHostAddress();
            else
                address = ((AAAARecord)record).getAddress().getHostAddress();

            String hostName = record.getName().toString();

            if (hostName == null || hostName.trim().length() == 0) continue;
            
            if(hostName.endsWith("."))
                hostName = hostName.substring(0, hostName.length() - 1);

            if (retHosts.keySet().contains(hostName)) {
                retHosts.get(hostName).addIPAddress(address);
            } else {
                DNSHostImpl hostToAdd = new DNSHostImpl(hostName);
                hostToAdd.addIPAddress(address);
                retHosts.put(hostName, hostToAdd);
            }
        }
        return new HashSet<DNSHost>(retHosts.values());
    }
}
