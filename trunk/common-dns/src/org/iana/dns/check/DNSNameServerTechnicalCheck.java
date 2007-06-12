package org.iana.dns.check;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSDomain;

/**
 * It provides a uniform interface for technical checks in the context of a single
 * name server of a given domain.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSNameServerTechnicalCheck {

    public void check(DNSNameServer ns) throws DNSTechnicalCheckException;
}
