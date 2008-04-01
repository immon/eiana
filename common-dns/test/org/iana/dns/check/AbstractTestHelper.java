package org.iana.dns.check;

import org.iana.dns.obj.DNSHostImpl;

/**
 * @author Piotr Tkaczyk
 */
public abstract class AbstractTestHelper {

    public DNSHostImpl createHost(String hostName, String... ipAddresses) {
        DNSHostImpl host = new DNSHostImpl(hostName);
        for (String ipAddress : ipAddresses) {
            host.addIPAddress(ipAddress);
        }

        return host;
    }

}
