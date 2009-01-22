package org.iana.dns.check;

import org.iana.dns.DNSDomain;

import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public abstract class AbstractDNSDomainTechnicalCheck implements DNSDomainTechnicalCheck {

    public DNSCheckResult check(DNSDomain domain, Set<DNSNameServer> nameServers) {
        try {
            doCheck(domain, nameServers);
        } catch (DNSTechnicalCheckException e) {
            return new DNSCheckSingleResult(this.getClass().getSimpleName(), e);
        }

        return new DNSCheckSingleResult(this.getClass().getSimpleName());
    }

    public abstract void doCheck(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException;
}
