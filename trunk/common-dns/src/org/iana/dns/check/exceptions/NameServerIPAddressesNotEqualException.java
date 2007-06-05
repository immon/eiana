package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class NameServerIPAddressesNotEqualException extends NameServerTechnicalCheckException {

    /**
     * Thrown in GlueCoherencyCheck when current ns and retrived from SOA ip's don't match
     *
     * @param host current host
     */
    public NameServerIPAddressesNotEqualException(DNSHost host) {
        super(host);
    }
}
