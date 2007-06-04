package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.*;
import org.iana.dns.validator.SpecialIPAddressChecker;
import org.iana.dns.whois.WhoIsDataRetriever;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class DomainNameServersCheck implements DNSDomainTechnicalCheck {

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
        checkNSSizeAndIPRestrictions(domain, nameServers);
        checkSerialNumberCoherency(domain, nameServers);
        checkMinimumNetworkDiversity(domain, nameServers);
        checkNameServerCoherency(domain, nameServers);
    }

    /*
     * Tests: Minimum name servers
     *        No private or reserved networks
     */
    public void checkNSSizeAndIPRestrictions(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
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

    /*
     * Tests: Minimum Network Diversity
     */
    public void checkMinimumNetworkDiversity(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {
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

    /*
     * Tests: Serial Number Coherency
     */
    public void checkSerialNumberCoherency(DNSDomain domain, Set<DNSNameServer> nameServers) throws MultipleDNSTechnicalCheckException {
        long serial = -1;
        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();
        for (DNSNameServer ns : nameServers) {
            if (!ns.isReachableByUDP()) {
                e.addException(new UnReachableByUDPException(ns.getName()));
            } else {
                long retSerial = ((SOARecord) ns.getSOA().getSectionArray(1)[0]).getSerial();
                if ((serial != -1) && (serial != retSerial))
                    e.addException(new SerialNumberNotEqualException(ns.getName(), retSerial));
                serial = retSerial;
            }
        }
        if (!e.isEmpty()) throw e;
    }

    /*
    * Tests: Name Server Coherency
    */
    public void checkNameServerCoherency(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {

        Set<String> retHostNames = new HashSet<String>();

        for (DNSNameServer ns : nameServers) {
            List<Record> nsRecords = Arrays.asList(ns.getSOA().getSectionArray(3));
            for (Record record : nsRecords)
                retHostNames.add(record.getName().toString());
        }

        Set<String> domainHostNames = new HashSet<String>();
        for (String name : domain.getNameServerNames())
            domainHostNames.add(name + ".");

        if (!retHostNames.equals(domainHostNames)) throw new NameServerCoherencyException();

    }

    private boolean isEmpty(Collection<?> col) {
        return col == null || col.isEmpty();
    }
}
