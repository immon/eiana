package org.iana.dns.check;

import org.iana.dns.check.exceptions.NotAuthoritativeNameServerException;

/**
 * (Test 5)
 * Checks that name server is authoritative for domain.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class NameServerAuthorityCheck extends NameServerCheckBase {

    public void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (!ns.isAuthoritative()) throw new NotAuthoritativeNameServerException(ns.getDomain(), ns.getHost());
    }
}
