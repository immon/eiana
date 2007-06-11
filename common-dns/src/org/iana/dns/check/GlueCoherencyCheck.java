package org.iana.dns.check;

import org.iana.dns.DNSIPAddress;
import org.iana.dns.check.exceptions.NameServerIPAddressesNotEqualException;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Record;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * (Test 7)
 * Checks A and AAAA records of authoritative name server,
 * and compares them to the supplied name server A and AAAA records.
 * These should match.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class GlueCoherencyCheck extends NameServerCheckBase {

    void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        Set<DNSIPAddress> retIpAddresses = new HashSet<DNSIPAddress>();
        for (Record record : ns.getAdditionalSection()) {
            String address = retrieveIPAddress(record, ns.getNameWithDot());
            if (address != null) retIpAddresses.add(DNSIPAddressImpl.createIPAddress(address));
        }
        if (!retIpAddresses.equals(ns.getIPAddresses())) throw new NameServerIPAddressesNotEqualException(ns.getHost());
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
