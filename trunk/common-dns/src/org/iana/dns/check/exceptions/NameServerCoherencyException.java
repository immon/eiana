package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;

/**
 * Thrown in NameServerCoherencyCheck when supplied name servers names don't match names returned in SOA.
 *
 * @author Piotr Tkaczyk
 */
public class NameServerCoherencyException extends DomainTechnicalCheckException {

    /**
     * Creates exception from given data.
     *
     * @param domain current domain
     */
    public NameServerCoherencyException(DNSDomain domain) {
        super(domain, null);
    }
}
