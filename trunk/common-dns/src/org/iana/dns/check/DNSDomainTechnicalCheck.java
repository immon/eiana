package org.iana.dns.check;

import org.iana.dns.DNSDomain;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DNSDomainTechnicalCheck {

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException;
    
}
