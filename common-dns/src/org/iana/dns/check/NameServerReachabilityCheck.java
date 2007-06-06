package org.iana.dns.check;

import org.iana.dns.check.exceptions.NameServerUnreachableByTCPException;
import org.iana.dns.check.exceptions.NameServerUnreachableByUDPException;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 *         <p/>
 *         (Test 4)
 *         Checks that name server is reachable by UDP and TCP.
 */

public class NameServerReachabilityCheck extends NameServerCheckBase {

    public void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (!ns.isReachableByUDP()) throw new NameServerUnreachableByUDPException(ns.getHost());
        if (!ns.isReachableByTCP()) throw new NameServerUnreachableByTCPException(ns.getHost());
    }
}
