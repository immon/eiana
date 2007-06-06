package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.exceptions.NameServerCoherencyException;
import org.xbill.DNS.Record;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 *         <p/>
 *         (Test 6)
 *         Checks that name servers returned by authoritative name server, and compare them to the supplied NS.
 *         These should match.
 */

public class NameServerCoherencyCheck implements DNSDomainTechnicalCheck {
    //todo diff in names
    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        Set<String> retHostNames = new HashSet<String>();

        for (DNSNameServer ns : nameServers) {
            for (Record record : ns.getAuthoritySection())
                retHostNames.add(removeLastDot(record.getAdditionalName().toString()));
        }

        if (!retHostNames.equals(domain.getNameServerNames())) throw new NameServerCoherencyException(domain);
    }

    public String removeLastDot(String name) {
        if (name.endsWith("."))
            return name.substring(0, name.length() - 1);
        else
            return name;
    }
}
