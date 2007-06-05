package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class NameServerUnreachableException extends NameServerTechnicalCheckException {

    /**
     * Thrown in NameServerCheckBase when SOA record in unreachable
     *
     * @param host current host
     */
    public NameServerUnreachableException(DNSHost host) {
        super(host);
    }
}
