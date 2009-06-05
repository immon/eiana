package org.iana.dns.check.exceptions;

import org.iana.dns.DNSDomain;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * Thrown in MinimumNameServersAndNoReservedIPsCheck when number of name servers is lower then requested.
 *
 * @author Piotr Tkaczyk
 */
public class NotEnoughNameServersException extends DomainTechnicalCheckException {

    private int expected;
    private int received;

    /**
     * Creates exception from given data.
     *
     * @param domain current domain
     */
    public NotEnoughNameServersException(DNSDomain domain, int expected, int received) {
        super(domain, null);
        this.expected = expected;
        this.received = received;
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptNotEnoughNameServersException(this);
    }

    public int getExpected() {
        return expected;
    }

    public int getReceived() {
        return received;
    }
}
