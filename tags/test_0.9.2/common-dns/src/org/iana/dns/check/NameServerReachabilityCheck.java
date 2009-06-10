package org.iana.dns.check;

import org.iana.dns.check.exceptions.NameServerUnreachableByTCPException;
import org.iana.dns.check.exceptions.NameServerUnreachableByUDPException;

/**
 * (Test 4)
 * Checks that name server is reachable by UDP and TCP.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */

public class NameServerReachabilityCheck extends NameServerCheckBase {

    public void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        MultipleDNSTechnicalCheckException e = new MultipleDNSTechnicalCheckException();
        if (!ns.isReachableByUDP()) e.addException(new NameServerUnreachableByUDPException(ns.getHost()));
        if (!ns.isReachableByTCP()) e.addException(new NameServerUnreachableByTCPException(ns.getHost()));
        if (!e.isEmpty()) throw e;
    }
}
