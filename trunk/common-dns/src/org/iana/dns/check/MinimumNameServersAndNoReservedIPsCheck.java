package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import static org.iana.dns.DNSIPAddress.Type.IPv4;
import org.iana.dns.DNSIPv4Address;
import org.iana.dns.check.exceptions.EmptyIPAddressListException;
import org.iana.dns.check.exceptions.NotEnoughNameServersException;
import org.iana.dns.check.exceptions.NotUniqueIPAddressException;
import org.iana.dns.check.exceptions.ReservedIPv4Exception;

import java.util.HashSet;
import java.util.Set;

/**
 * (Test 1, 11)
 * There must be at least two name server host with at least one IPv4 or IPv6 address,
 * and they must not share the same IP. These IP's must not be any RFC 3330 addresses.
 *
 * @author Piotr Tkaczyk
 */
public class MinimumNameServersAndNoReservedIPsCheck extends AbstractDNSDomainTechnicalCheck {

    private int minNameServersNumber;

    public MinimumNameServersAndNoReservedIPsCheck() {
        minNameServersNumber = 2;
    }

    public MinimumNameServersAndNoReservedIPsCheck(int minNameServersNumber) {
        this.minNameServersNumber = minNameServersNumber;
    }

    public void doCheck(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {

        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();

        Set<String> uniqueIPv4Addresses = new HashSet<String>();
        int uniqueDiff = 0;

        if (nameServers == null || nameServers.isEmpty())
            throw new NotEnoughNameServersException(domain);

        for (DNSNameServer currentNs : nameServers) {
            Set<DNSIPAddress> currentIps = currentNs.getIPAddresses();
            if (currentIps.isEmpty()) {
                e.addException(new EmptyIPAddressListException(domain, currentNs.getHost()));
            } else {
                int uniqueSize = uniqueIPv4Addresses.size();
                for (DNSIPAddress ipAddress : currentIps) {
                    if ((ipAddress.getType() == IPv4)) {
                        if (((DNSIPv4Address) ipAddress).isReserved()) {
                            e.addException(new ReservedIPv4Exception(domain, currentNs.getHost(), ipAddress));
                        } else {
                            uniqueIPv4Addresses.add(ipAddress.getAddress());
                        }
                    }
                }
                if (uniqueSize < uniqueIPv4Addresses.size())
                    uniqueDiff++;

                for (DNSNameServer otherNs : nameServers) {
                    if (!otherNs.getName().equals(currentNs.getName())) {
                        Set<DNSIPAddress> otherIps = otherNs.getIPAddresses();
                        if (otherIps.containsAll(currentIps))
                            e.addException(new NotUniqueIPAddressException(domain, currentNs.getHost(), otherNs.getHost()));
                    }
                }
            }
        }
        if (uniqueDiff < minNameServersNumber)
            e.addException(new NotEnoughNameServersException(domain));

        if (!e.isEmpty()) throw e;
    }
}
