package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.validator.SpecialIPAddressChecker;
import org.xbill.DNS.SOARecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */
public class DomainNameServersCheck implements DNSDomainTechnicalCheck {
    //todo better exceptions
    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        checkNSSizeAndIPRestrictions(domain, nameServers);
        checkSerialNumberCoherency(domain, nameServers);
    }

    public void checkNSSizeAndIPRestrictions(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        if ((nameServers == null) || (nameServers.size() < 2)) throw new DNSTechnicalCheckException(); //todo

        List<String> ipAddresses = new ArrayList<String>();

        for (DNSNameServer nameServer : nameServers) {
            if (isEmpty(nameServer.getIPAddresses())) throw new DNSTechnicalCheckException(); //todo

            for (DNSIPAddress ipAddress : nameServer.getIPAddresses()) {
                if (ipAddresses.contains(ipAddress.getAddress())) throw new DNSTechnicalCheckException(); //todo

                if ((ipAddress.getType().equals(DNSIPAddress.Type.IPv4)) &&
                        (SpecialIPAddressChecker.isAllocatedForSpecialUse(ipAddress.getAddress())))
                    throw new DNSTechnicalCheckException();
            }

            ipAddresses.addAll(nameServer.getIPAddressesAsStrings());
        }
    }

    public void checkSerialNumberCoherency(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        long serial = -1;
        for (DNSNameServer ns : nameServers) {
            if (!ns.isReachableByUDP()) throw new DNSTechnicalCheckException();
            long retSerial = ((SOARecord) ns.getSOA().getSectionArray(1)[0]).getSerial();
            if ((serial != -1) && (serial != retSerial)) throw new DNSTechnicalCheckException();
            serial = retSerial;
        }
    }

    private boolean isEmpty(Collection<?> col) {
        return col == null || col.isEmpty();
    }
}
