package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */
public class NotAuthoritativeNameServerException extends NameServerTechnicalCheckException {

    /**
     * Thrown in NameServerAuthorityCheck when host is not authoritative;
     *
     * @param host - current host
     */

    public NotAuthoritativeNameServerException(DNSHost host) {
        super(host);
    }
}
