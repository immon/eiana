package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.check.exceptions.SerialNumberNotEqualException;

import java.util.*;

/**
 * (Test 8)
 * Checks the serial numbers in the SOA records supplied by the authoritative name servers.
 * These should match.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class SerialNumberCoherencyCheck implements DNSDomainTechnicalCheck {

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {

        Map<Long, List<DNSHost>> serialsMap = new HashMap<Long, List<DNSHost>>();

        for (DNSNameServer ns : nameServers) {
            Long retSerial = ns.getSerialNumber();
            if (serialsMap.keySet().contains(retSerial)) {
                List<DNSHost> host = serialsMap.get(retSerial);
                host.add(ns.getHost());
                serialsMap.put(retSerial, host);
            } else {
                List<DNSHost> hosts = new ArrayList<DNSHost>();
                hosts.add(ns.getHost());
                serialsMap.put(retSerial, hosts);
            }
        }
        if (serialsMap.size() > 1)
            throw new SerialNumberNotEqualException(domain, serialsMap);

    }
}
