package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;

/**
 * @author Piotr Tkaczyk
 */
public class NameServerCoherencyException extends DomainTechnicalCheckException {

    /**
     * Thrown in NameServerCoherencyCheck when supplied name servers names don't match names returned in SOA;
     *
     * @param domain - current domain
     */
    public NameServerCoherencyException(DNSDomain domain) {
        super(domain, null);
    }
}
