package org.iana.dns.check.exceptions;

import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * @author Piotr Tkaczyk
 */
public class DNSCheckIOException extends DNSTechnicalCheckException {

    String message;

    public DNSCheckIOException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        visitor.acceptDNSCheckIOException(this);
    }
}
