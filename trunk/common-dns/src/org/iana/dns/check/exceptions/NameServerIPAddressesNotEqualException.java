package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * Thrown in GlueCoherencyCheck when current NS IP addresses and retrived from SOA record don't match.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerIPAddressesNotEqualException extends NameServerTechnicalCheckException {

    /**
     * Creates exception from given data.
     *
     * @param host current host
     */
    public NameServerIPAddressesNotEqualException(DNSHost host) {
        super(host);
    }
}
