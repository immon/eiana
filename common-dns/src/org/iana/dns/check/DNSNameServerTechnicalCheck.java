package org.iana.dns.check;

import org.iana.dns.DNSHost;
import org.iana.dns.DNSDomain;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface DNSNameServerTechnicalCheck {

    public void check(DNSNameServer ns) throws DNSTechnicalCheckException;
}
