package org.iana.dns.check;

import org.apache.log4j.Logger;
import org.iana.dns.check.exceptions.NameServerUnreachableException;

/**
 * Parent class for name server technical checks.
 *
 * @author Piotr Tkaczyk
 */
abstract class NameServerCheckBase implements DNSNameServerTechnicalCheck {

    public DNSCheckResult check(DNSNameServer ns) {
        try {
            if (ns.getSOA() != null) {
                try{
                    doCheck(ns);
                } catch (DNSTechnicalCheckException e) {
                    return new DNSCheckSingleResult(this.getClass().getSimpleName(), e);
                }
                return new DNSCheckSingleResult(this.getClass().getSimpleName());
            } else {
                Logger.getLogger(NameServerCheckBase.class).warn("null SOA for " + ns.getName());
                NameServerUnreachableException e = new NameServerUnreachableException(ns.getHost());
                return new DNSCheckSingleResult(this.getClass().getSimpleName(), e);
            }
        } catch (NameServerUnreachableException e) {
            Logger.getLogger(NameServerCheckBase.class).warn("null SOA for " + ns.getName());
            return new DNSCheckSingleResult(this.getClass().getSimpleName(), e);    
        }
    }

    abstract void doCheck(DNSNameServer ns) throws DNSTechnicalCheckException;
}
