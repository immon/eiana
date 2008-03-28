package org.iana.dns.check;

import org.iana.dns.DNSDomain;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmptyDNSTechnicalCheck extends DNSTechnicalCheck {

    public void check(DNSDomain domain) throws DNSTechnicalCheckException {
    }

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
    }
}
