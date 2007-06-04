package org.iana.dns.check;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.exceptions.SerialNumberNotEqualException;
import org.iana.dns.check.exceptions.UnReachableByUDPException;
import org.xbill.DNS.SOARecord;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
public class SerialNumberCoherency implements DNSDomainTechnicalCheck {

    /*
    * Tests: Serial Number Coherency
    */

    public void check(DNSDomain domain, Set<DNSNameServer> nameServers) throws DNSTechnicalCheckException {

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
}
