package org.iana.dns.check;

import org.iana.dns.DNSIPAddress;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Record;

import java.util.*;

/**
 * @author: Piotr Tkaczyk
 */
public class NameServerChecks extends NameServerChecksBase {
    //todo better exceptions
    public void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        checkReachability(ns);
        checkAuthority(ns);
        checkNameServerCoherency(ns);
        checkNameServerGlueCoherency(ns);
    }

    public void checkAuthority(DNSNameServer ns) throws DNSTechnicalCheckException {
        ns.isAuthoritative();
    }

    public void checkReachability(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (!ns.isReachableByTCP()) throw new DNSTechnicalCheckException();
        if (!ns.isReachableByUDP()) throw new DNSTechnicalCheckException();
    }

    public void checkNameServerCoherency(DNSNameServer ns) throws DNSTechnicalCheckException {

        List<Record> nsRecords = Arrays.asList(ns.getSOA().getSectionArray(3));

        Set<String> retHostNames = new HashSet<String>();

        for (Record record : nsRecords)
            retHostNames.add(record.getName().toString());


        Set<String> domainHostNames = new HashSet<String>();
        for (String name : ns.getDomain().getNameServerNames())
            domainHostNames.add(name + ".");

        if (!retHostNames.equals(domainHostNames)) throw new DNSTechnicalCheckException();

    }

    public void checkNameServerGlueCoherency(DNSNameServer ns) throws DNSTechnicalCheckException {
        List<Record> nsRecords = Arrays.asList(ns.getSOA().getSectionArray(3));
        Set<DNSIPAddress> retIpAddresses = new HashSet<DNSIPAddress>();
        for (Record record : nsRecords) {
            String address = retrieveIPAddress(record, ns.getNameWithDot());
            if (address != null) retIpAddresses.add(DNSIPAddressImpl.createIPAddress(address));
        }
        if (!retIpAddresses.equals(ns.getIPAddresses())) throw new DNSTechnicalCheckException();
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
