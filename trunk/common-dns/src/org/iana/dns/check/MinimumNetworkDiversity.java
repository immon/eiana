package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.DuplicatedASNumberException;
import org.iana.dns.check.exceptions.NoASNumberException;
import org.iana.dns.whois.WhoIsDataRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
public class MinimumNetworkDiversity implements DNSDomainTechnicalCheck {

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        List<String> asNumbers = new ArrayList<String>();

        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();
        for (DNSNameServer ns : nameServers) {
            for (DNSIPAddress ipAddress : ns.getIPAddresses()) {
                if (ipAddress.getType().equals(DNSIPAddress.Type.IPv4)) {
                    String asNumber = new WhoIsDataRetriever().retrieveASNumber(ipAddress.getAddress());

                    if ("".equals(asNumber))
                        e.addException(new NoASNumberException(ns.getName(), ipAddress.getAddress()));

                    if (asNumbers.contains(asNumber)) {
                        e.addException(new DuplicatedASNumberException(ns.getName(), ipAddress.getAddress()));
                    } else {
                        asNumbers.add(asNumber);
                    }
                }
            }
        }

        if (!e.isEmpty()) throw e;
    }
}
