package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.DuplicatedIPAddressException;
import org.iana.dns.check.exceptions.EmptyIPAddressListException;
import org.iana.dns.check.exceptions.NotEnoughNameServersException;
import org.iana.dns.check.exceptions.RestrictedIPv4Exception;
import org.iana.dns.validator.SpecialIPAddressChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */
public class NSCountAndIPRestrictions implements DNSDomainTechnicalCheck {


    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();

        if ((nameServers == null) || (nameServers.size() < 2)) throw new NotEnoughNameServersException();

        List<String> ipAddresses = new ArrayList<String>();

        for (DNSNameServer nameServer : nameServers) {
            if (isEmpty(nameServer.getIPAddresses())) {
                e.addException(new EmptyIPAddressListException(nameServer.getName()));
            } else {
                for (DNSIPAddress ipAddress : nameServer.getIPAddresses()) {
                    if (ipAddresses.contains(ipAddress.getAddress())) {
                        e.addException(new DuplicatedIPAddressException(nameServer.getName(), ipAddress.getAddress()));
                    } else {
                        if ((ipAddress.getType().equals(DNSIPAddress.Type.IPv4)) &&
                                (SpecialIPAddressChecker.isAllocatedForSpecialUse(ipAddress.getAddress())))
                            e.addException(new RestrictedIPv4Exception(nameServer.getName(), ipAddress.getAddress()));
                    }
                }
                ipAddresses.addAll(nameServer.getIPAddressesAsStrings());
            }
        }
        if (!e.isEmpty()) throw e;
    }

    private boolean isEmpty(Collection<?> col) {
        return col == null || col.isEmpty();
    }
}
