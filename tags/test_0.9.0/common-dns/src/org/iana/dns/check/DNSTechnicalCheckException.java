package org.iana.dns.check;

/**
 * A root of a hierarchy of exceptions that are thrown by various DNS technical checks.
 *
 * @author Patrycja Wegrzynowicz
 */
public abstract class DNSTechnicalCheckException extends Exception {
    public abstract void accept(DNSTechnicalCheckExceptionVisitor visitor);
}
