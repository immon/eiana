package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * Thrown in NameServerReachabilityCheck when SOA record in unreachable by UDP.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerUnreachableByUDPException extends NameServerUnreachableException {

    /**
     * Creates exception from given data.
     *
     * @param host current host
     */
    public NameServerUnreachableByUDPException(DNSHost host) {
        super(host);
    }
}
