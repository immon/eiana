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
    private DNSHost otherHost;

    public NotUniqueIPAddressException(DNSDomain domain, DNSHost currentHost, DNSHost otherHost) {
        super(domain, currentHost);
        this.otherHost = otherHost;
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNotUniqueIPAddressException(this);
    }

    public DNSHost getOtherHost() {
        return otherHost;
    }
}
