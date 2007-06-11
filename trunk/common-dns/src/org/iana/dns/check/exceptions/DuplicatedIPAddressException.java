package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;

/**
 * Thrown in MinimumNameServersAndNoReservedIPsCheck when IP address is duplicated.
 *
 * @author Piotr Tkaczyk
 */
public class DuplicatedIPAddressException extends DomainTechnicalCheckException {

    DNSIPAddress ipAddress;

    /**
     * Creates exception from given data.
     *
     * @param domain    current domain
     * @param host      current host
     * @param ipAddress duplicated IP address
     */
    public DuplicatedIPAddressException(DNSDomain domain, DNSHost host, DNSIPAddress ipAddress) {
        super(domain, host);
        this.ipAddress = ipAddress;
    }

    public DNSIPAddress getIpAddress() {
        return ipAddress;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DuplicatedIPAddressException that = (DuplicatedIPAddressException) o;

        if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
        return result;
    }
}
