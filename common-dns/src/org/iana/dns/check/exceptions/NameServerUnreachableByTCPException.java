package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class NameServerUnreachableByTCPException extends NameServerUnreachableException {

    /**
     * Thrown in NameServerReachabilityCheck when SOA record in unreachable by TCP;
     *
     * @param host - current host
     */

    public NameServerUnreachableByTCPException(DNSHost host) {
        super(host);
    }
}
