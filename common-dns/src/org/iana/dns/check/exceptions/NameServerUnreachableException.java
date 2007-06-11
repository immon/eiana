package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * Thrown in NameServerCheckBase when SOA record in unreachable.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerUnreachableException extends NameServerTechnicalCheckException {

    /**
     * Creates exception from given data.
     *
     * @param host current host
     */
    public NameServerUnreachableException(DNSHost host) {
        super(host);
    }
}
