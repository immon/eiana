package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Thrown in MinimumNameServersAndNoReservedIPsCheck when IP address is duplicated.
 *
 * @author Piotr Tkaczyk
 */
public class NotUniqueIPAddressException extends DomainTechnicalCheckException {

    /**
     * Creates exception from given data.
     *
     * @param domain    current domain
     * @param host      current host
     */
    public NotUniqueIPAddressException(DNSDomain domain, DNSHost host) {
        super(domain, host);
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNotUniqueIPAddressException(this);
    }
}
