package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class DuplicatedASNumberException extends DomainTechnicalCheckException {

    private String asNumber;
    private List<DNSHost> hosts;

    /**
     * Thrown when two or more name servers have the same AS numbers in MinimumNetworkDiversityCheck.
     *
     * @param domain   - current domain
     * @param asNumber - duplicated AS number
     * @param hosts    - hosts list with this asNumber
     */
    public DuplicatedASNumberException(DNSDomain domain, String asNumber, List<DNSHost> hosts) {
        super(domain, null);
        this.asNumber = asNumber;
        this.hosts = hosts;
    }

    public String getASNumber() {
        return asNumber;
    }

    public List<DNSHost> getHosts() {
        return hosts;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DuplicatedASNumberException that = (DuplicatedASNumberException) o;

        if (asNumber != null ? !asNumber.equals(that.asNumber) : that.asNumber != null) return false;
        if (hosts != null ? !hosts.equals(that.hosts) : that.hosts != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (asNumber != null ? asNumber.hashCode() : 0);
        result = 31 * result + (hosts != null ? hosts.hashCode() : 0);
        return result;
    }
}
