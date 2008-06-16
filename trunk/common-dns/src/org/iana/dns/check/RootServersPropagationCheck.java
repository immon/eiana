package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.exceptions.RootServersPropagationException;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.rzm.common.validators.CheckTool;
import org.xbill.DNS.Record;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.AAAARecord;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
public class RootServersPropagationCheck implements DNSDomainTechnicalCheck {

    List<DNSHost> rootServers = new ArrayList<DNSHost>();
    private int dnsCheckRetries;

    public void setRootServers(List<DNSHost> rootServers) {
        this.rootServers = rootServers;
    }

    public void setDnsCheckRetries(int dnsCheckRetries) {
        this.dnsCheckRetries = dnsCheckRetries;
    }

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        CheckTool.checkNull(rootServers, "null root servers");
        for( DNSHost dnsHost : rootServers) {
            DNSNameServer nameServer = new DNSNameServer(domain, dnsHost, dnsCheckRetries);
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
