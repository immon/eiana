package org.iana.dns.check;

import org.iana.dns.check.exceptions.NotAuthoritativeNameServerException;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public class NameServerAuthorityCheck extends NameServerCheckBase {

    public void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (!ns.isAuthoritative()) throw new NotAuthoritativeNameServerException(ns.getHost());
    }
}
