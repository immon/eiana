package org.iana.dns.check;

import org.iana.dns.check.exceptions.NotAuthoritativeNameServerException;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 *         <p/>
 *         (Test 5)
 *         Checks that name server is authoritative.
 */
public class NameServerAuthorityCheck extends NameServerCheckBase {

    public void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (!ns.isAuthoritative()) throw new NotAuthoritativeNameServerException(ns.getHost());
    }
}
