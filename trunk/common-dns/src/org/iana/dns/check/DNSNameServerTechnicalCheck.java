package org.iana.dns.check;

/**
 * It provides a uniform interface for technical checks in the context of a single
 * name server of a given domain.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSNameServerTechnicalCheck {

    public DNSCheckResult check(DNSNameServer ns);
}
