package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class NameServerUnreachableByUDPException extends NameServerUnreachableException {

    /**
     * Thrown in NameServerReachabilityCheck when SOA record in unreachable by UDP
     *
     * @param host current host
     */

    public NameServerUnreachableByUDPException(DNSHost host) {
        super(host);
    }
}
