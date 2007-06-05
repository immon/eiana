package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import static org.iana.dns.DNSIPAddress.Type.IPv4;
import org.iana.dns.DNSIPv4Address;
import org.iana.dns.check.exceptions.DuplicatedIPAddressException;
import org.iana.dns.check.exceptions.EmptyIPAddressListException;
import org.iana.dns.check.exceptions.NotEnoughNameServersException;
import org.iana.dns.check.exceptions.ReservedIPv4Exception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */
public class MinimumNameServersAndNoReservedIPsCheck implements DNSDomainTechnicalCheck {

    private int minNameServersNumber;

    public MinimumNameServersAndNoReservedIPsCheck() {
        minNameServersNumber = 2;
    }

    public MinimumNameServersAndNoReservedIPsCheck(int minNameServersNumber) {
        this.minNameServersNumber = minNameServersNumber;
    }

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();

        if ((nameServers == null) || (nameServers.size() < minNameServersNumber))
            throw new NotEnoughNameServersException(domain);

        List<String> ipAddresses = new ArrayList<String>();

        for (DNSNameServer ns : nameServers) {
            if (isEmpty(ns.getIPAddresses())) {
                e.addException(new EmptyIPAddressListException(domain, ns.getHost()));
            } else {
                for (DNSIPAddress ipAddress : ns.getIPAddresses()) {
                    if (ipAddresses.contains(ipAddress.getAddress())) {
                        e.addException(new DuplicatedIPAddressException(domain, ns.getHost(), ipAddress));
                    } else {
                        if ((ipAddress.getType() == IPv4) && ((DNSIPv4Address) ipAddress).isReserved())
                            e.addException(new ReservedIPv4Exception(domain, ns.getHost(), ipAddress));
                    }
                }
                ipAddresses.addAll(ns.getIPAddressesAsStrings());
            }
        }
        if (!e.isEmpty()) throw e;
    }

    private boolean isEmpty(Collection<?> col) {
        return col == null || col.isEmpty();
    }
}
