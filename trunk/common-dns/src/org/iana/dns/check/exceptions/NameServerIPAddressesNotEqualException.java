package org.iana.dns.check.exceptions;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

import java.util.Set;

/**
 * Thrown in GlueCoherencyCheck when current NS IP addresses and retrived from SOA record don't match.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerIPAddressesNotEqualException extends NameServerTechnicalCheckException {

    private Set<DNSIPAddress> dnsIPAddresses;

    /**
     * Creates exception from given data.
     *
     * @param host           current host
     * @param dnsIPAddresses list of ip addresses returned in check
     */
    public NameServerIPAddressesNotEqualException(DNSHost host, Set<DNSIPAddress> dnsIPAddresses) {
        super(host);
        this.dnsIPAddresses = dnsIPAddresses;
    }

    public Set<DNSIPAddress> getReturnedIPAddresses() {
        return dnsIPAddresses;
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNameServerIPAddressesNotEqualException(this);
    }
}
