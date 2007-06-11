package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Thrown in SerialNumberCoherencyCheck when there are name servers with more then one serial number.
 *
 * @author Piotr Tkaczyk
 */
public class SerialNumberNotEqualException extends DomainTechnicalCheckException {

    Map<Long, List<DNSHost>> serialsMap;

    /**
     * Creates exception from given data.
     *
     * @param domain     current domain
     * @param serialsMap maps serial numbers to name servers
     */
    public SerialNumberNotEqualException(DNSDomain domain, Map<Long, List<DNSHost>> serialsMap) {
        super(domain, null);
        this.serialsMap = serialsMap;
    }

    public Map<Long, List<DNSHost>> getSerialsMap() {
        return serialsMap;
    }

    public Set<Long> getSerialNumbers() {
        return serialsMap.keySet();
    }

    public List<DNSHost> getHosts(Long serialNumber) {
        return serialsMap.get(serialNumber);
    }
}
