package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;

/**
 * @author Piotr Tkaczyk
 */

public class NotEnoughNameServersException extends DomainTechnicalCheckException {

    /**
     * Thrown in MinimumNameServersAndNoReservedIPsCheck when number of name servers is lower then requested;
     *
     * @param domain - current domain
     */
    public NotEnoughNameServersException(DNSDomain domain) {
        super(domain, null);
    }
}
