package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;

/**
 * Thrown in MinimumNameServersAndNoReservedIPsCheck when number of name servers is lower then requested.
 *
 * @author Piotr Tkaczyk
 */
public class NotEnoughNameServersException extends DomainTechnicalCheckException {

    /**
     * Creates exception from given data.
     *
     * @param domain current domain
     */
    public NotEnoughNameServersException(DNSDomain domain) {
        super(domain, null);
    }
}
