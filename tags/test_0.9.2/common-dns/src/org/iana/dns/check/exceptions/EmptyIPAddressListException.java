package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Thrown in MinimumNameServersAndNoReservedIPsCheck when host don't have any IP's.
 *
 * @author Piotr Tkaczyk
 */
public class EmptyIPAddressListException extends DomainTechnicalCheckException {

    /**
     * Creates exception from given data.
     *
     * @param domain current domain
     * @param host   current host
     */
    public EmptyIPAddressListException(DNSDomain domain, DNSHost host) {
        super(domain, host);
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptEmptyIPAddressListException(this);
    }
}
