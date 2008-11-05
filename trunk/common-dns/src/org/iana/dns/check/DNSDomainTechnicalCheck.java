package org.iana.dns.check;

import org.iana.dns.DNSDomain;

import java.util.Set;

/**
 * It provides a uniform interface for technical checks in the context of a domain (i.e.
 * performed on the domain itself, often including all name servers).
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public interface DNSDomainTechnicalCheck {

    public DNSCheckResult check(DNSDomain domain, Set<DNSNameServer> nameServers);
    
}
