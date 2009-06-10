package org.iana.rzm.trans.check;

import org.iana.dns.obj.DNSDomainImpl;
import org.iana.dns.obj.DNSHostImpl;
import org.iana.dns.obj.DNSIPv4AddressImpl;
import org.iana.dns.obj.DNSIPv6AddressImpl;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.ObjectConfiguration;

/**
 * @author Piotr Tkaczyk
 */
public class RadicalAlterationDiffConfiguration extends DiffConfiguration {

    public RadicalAlterationDiffConfiguration() {
        ObjectConfiguration dnsDomainConfig = new ObjectConfiguration(new String [] {"name", "nameServers"}, "name");
        dnsDomainConfig.addFieldClass("nameServers", DNSHostImpl.class);
        addObjectConfiguration(DNSDomainImpl.class, dnsDomainConfig);

        ObjectConfiguration dnsHostConfig = new ObjectConfiguration(new String[] { "name", "iPAddresses"}, "name");
        addObjectConfiguration(DNSHostImpl.class, dnsHostConfig);

        ObjectConfiguration dnsIpAddress = new ObjectConfiguration(new String[] { "address", "type"} , "address");
        addObjectConfiguration(DNSIPv4AddressImpl.class, dnsIpAddress);
        addObjectConfiguration(DNSIPv6AddressImpl.class, dnsIpAddress);

    }
}
