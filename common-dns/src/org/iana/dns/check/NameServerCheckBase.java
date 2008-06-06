package org.iana.dns.check;

import org.apache.log4j.Logger;
import org.iana.dns.check.exceptions.NameServerUnreachableException;

/**
 * Parent class for name server technical checks.
 *
 * @author Piotr Tkaczyk
 */
abstract class NameServerCheckBase implements DNSNameServerTechnicalCheck {

    public void check(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (ns.getSOA() != null) {
            doCheck(ns);
        } else {
            Logger.getLogger(NameServerCheckBase.class).warn("null SOA for " + ns.getName());
            throw new NameServerUnreachableException(ns.getHost());
        }
    }

    abstract void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException;
}
