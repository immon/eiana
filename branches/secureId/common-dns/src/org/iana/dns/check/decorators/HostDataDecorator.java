package org.iana.dns.check.decorators;

import org.iana.dns.DNSHost;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Piotr Tkaczyk
 */
public class HostDataDecorator {

    DNSHost host;

    public HostDataDecorator(DNSHost host) {
        this.host = host;
    }

    public String getName() {
        return host.getName();
    }

    public List<String> getIpAddresses() {
        List<String> ret = new ArrayList<String>();
        for (String ipAddress : host.getIPAddressesAsStrings())
            ret.add(ipAddress);

        return ret;
    }
}
