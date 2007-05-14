package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class SendNSQueryException extends DomainCheckException {
    Throwable cause;

    public SendNSQueryException(Throwable cause) {
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
