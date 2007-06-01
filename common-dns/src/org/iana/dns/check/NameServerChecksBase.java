package org.iana.dns.check;

/**
 * @author: Piotr Tkaczyk
 */
abstract class NameServerChecksBase implements DNSNameServerTechnicalCheck {

    public void check(DNSNameServer ns) throws DNSTechnicalCheckException {
        if (ns.getSOA() != null)
            doCheck(ns);
    }

    abstract void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException;
}
