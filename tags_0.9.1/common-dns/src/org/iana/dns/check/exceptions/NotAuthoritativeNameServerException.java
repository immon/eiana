package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Thrown in NameServerAuthorityCheck when host is not authoritative.
 *
 * @author Piotr Tkaczyk
 */
public class NotAuthoritativeNameServerException extends NameServerTechnicalCheckException {

    private DNSDomain domain;

    /**
     * Creates exception from given data.
     *
     * @param domain curent domain
     * @param host   current host
     */
    public NotAuthoritativeNameServerException(DNSDomain domain, DNSHost host) {
        super(host);
        this.domain = domain;
    }

    public String getDomainName() {
        return domain.getName();
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNotAuthoritativeNameServerException(this);
    }
}
