package org.iana.dns.check;

/**
 * A root of a hierarchy of exceptions that are thrown by various DNS technical checks.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public abstract class DNSTechnicalCheckException extends Exception {

    protected DNSTechnicalCheckException() {
    }

    protected DNSTechnicalCheckException(Throwable cause) {
        super(cause);
    }

    public abstract void accept(DNSTechnicalCheckExceptionVisitor visitor);

}
