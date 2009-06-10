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


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NotUniqueIPAddressException that = (NotUniqueIPAddressException) o;

        if (otherHost != null ? !otherHost.equals(that.otherHost) : that.otherHost != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (otherHost != null ? otherHost.hashCode() : 0);
        return result;
    }
}
