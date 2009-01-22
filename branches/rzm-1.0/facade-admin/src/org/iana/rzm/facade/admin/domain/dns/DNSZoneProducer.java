package org.iana.rzm.facade.admin.domain.dns;

import org.iana.dns.DNSZone;

/**
 * This interface produces a zone to export. 
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DNSZoneProducer {

    public DNSZone getDNSZone();

}
