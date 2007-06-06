package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSHost;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.DNSWhoIsData;
import org.iana.dns.check.exceptions.DuplicatedASNumberException;
import org.iana.dns.check.exceptions.NoASNumberException;
import org.iana.dns.check.exceptions.WhoIsIOException;
import org.iana.dns.whois.WhoIsDataRetriever;

import java.io.IOException;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 *         <p/>
 *         (Test 9)
 *         Checks that provided IP addresses originate from at least two different autonomous systems.
 */
public class MinimumNetworkDiversityCheck implements DNSDomainTechnicalCheck {

    DNSWhoIsData whoIs;

    public MinimumNetworkDiversityCheck() {
        whoIs = new WhoIsDataRetriever();
    }

    public MinimumNetworkDiversityCheck(DNSWhoIsData whoIs) {
        this.whoIs = whoIs;
    }

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
//          ASNumber, DNSHost
        Map<String, List<DNSHost>> asNumbers = new HashMap<String, List<DNSHost>>();

        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();
        for (DNSNameServer ns : nameServers) {
            for (DNSIPAddress ipAddress : ns.getIPAddresses()) {
                try {
                    String asNumber = whoIs.retrieveASNumber(ipAddress.getAddress());
                    if ("".equals(asNumber)) {
                        e.addException(new NoASNumberException(domain, ns.getHost(), ipAddress));
                    } else {
                        if (asNumbers.keySet().contains(asNumber)) {
                            List<DNSHost> hostsList = asNumbers.get(asNumber);
                            if (!hostsList.contains(ns.getHost())) {
                                hostsList.add(ns.getHost());
                                asNumbers.put(asNumber, hostsList);
                            }
                        } else {
                            List<DNSHost> hostsList = new ArrayList<DNSHost>();
                            hostsList.add(ns.getHost());
                            asNumbers.put(asNumber, hostsList);
                        }
                    }
                } catch (IOException ioException) {
                    e.addException(new WhoIsIOException(ns.getHost(), ipAddress, ioException.getCause()));
                }
            }
        }

        for (String asNumber : asNumbers.keySet()) {
            if (asNumbers.get(asNumber).size() > 1)
                e.addException(new DuplicatedASNumberException(domain, asNumber, asNumbers.get(asNumber)));
        }

        if (!e.isEmpty()) throw e;
    }
}
