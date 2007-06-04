package org.iana.dns.check;

import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.NameServerIPAddressesNotEqualException;
import org.iana.dns.check.exceptions.NotAuthoritativeNameServerException;
import org.iana.dns.check.exceptions.UnReachableByTCPException;
import org.iana.dns.check.exceptions.UnReachableByUDPException;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Record;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class NameServerChecks extends NameServerChecksBase {

    /*
     * Tests: Name Server Reachability
     *        Name Server Authority
     *        Name Server Coherency
     *        Name Server Glue Coherency
     */

    public void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        checkReachability(ns);
        checkAuthority(ns);
        checkNameServerGlueCoherency(ns);
    }

    public void checkReachability(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (!ns.isReachableByUDP()) throw new UnReachableByUDPException(ns.getName());
        if (!ns.isReachableByTCP()) throw new UnReachableByTCPException(ns.getName());
    }

    public void checkAuthority(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (!ns.isAuthoritative()) throw new NotAuthoritativeNameServerException(ns.getName());
    }

    public void checkNameServerGlueCoherency(DNSNameServer ns) throws DNSTechnicalCheckException {
        List<Record> nsRecords = Arrays.asList(ns.getSOA().getSectionArray(3));
        Set<DNSIPAddress> retIpAddresses = new HashSet<DNSIPAddress>();
        for (Record record : nsRecords) {
            String address = retrieveIPAddress(record, ns.getNameWithDot());
            if (address != null) retIpAddresses.add(DNSIPAddressImpl.createIPAddress(address));
        }
        if (!retIpAddresses.equals(ns.getIPAddresses())) throw new NameServerIPAddressesNotEqualException(ns.getName());
    }

    private static String retrieveIPAddress(Record record, String hostName) {
        hostName = hostName.toLowerCase(Locale.ENGLISH);
        if (record instanceof ARecord) {
            ARecord aRecord = (ARecord) record;
            if (aRecord.getName().toString().toLowerCase(Locale.ENGLISH).equals(hostName)) {
                String ipAddress = aRecord.getAddress().toString();
                return ipAddress.replaceFirst("/", "");
            }
        }
        if (record instanceof AAAARecord) {
            AAAARecord a4Record = (AAAARecord) record;
            if (a4Record.getName().toString().toLowerCase(Locale.ENGLISH).equals(hostName)) {
                String ipAddress = a4Record.getAddress().toString();
                return ipAddress.replaceFirst("/", "");
            }
        }
        return null;
    }

}
