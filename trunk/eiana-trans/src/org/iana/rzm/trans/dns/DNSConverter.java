package org.iana.rzm.trans.dns;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.IPAddress;

/**
 * @author Jakub Laszkiewicz
 */
public class DNSConverter {
    public static DNSDomain toDNSDomain(Domain domain) {
        DNSDomainImpl result = new DNSDomainImpl(domain.getName());
        for (Host host : domain.getNameServers()) {
            DNSHostImpl dnsHost = new DNSHostImpl(host.getName());
            for (IPAddress ipAddress : host.getAddresses()) {
                DNSIPAddress dnsIpAddress;
                if (ipAddress.isIPv4())
                    dnsIpAddress = DNSIPAddressImpl.createIPv4Address(ipAddress.getAddress());
                else
                    dnsIpAddress = DNSIPAddressImpl.createIPv6Address(ipAddress.getAddress());
                dnsHost.addIPAddress(dnsIpAddress);
            }
            result.addNameServer(dnsHost);
        }
        return result;
    }
}
