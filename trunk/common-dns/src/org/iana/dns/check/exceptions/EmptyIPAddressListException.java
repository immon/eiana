package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

/**
 * @author Piotr Tkaczyk
 */

public class EmptyIPAddressListException extends DomainTechnicalCheckException {

    /**
     * Thrown in MinimumNameServersAndNoReservedIPsCheck when host don't have any IP's;
     *
     * @param domain - current domain
     * @param host   - current host
     */
    public EmptyIPAddressListException(DNSDomain domain, DNSHost host) {
        super(domain, host);
    }
}
