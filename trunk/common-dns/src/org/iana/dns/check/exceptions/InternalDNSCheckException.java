package org.iana.dns.check.exceptions;

import org.iana.dns.check.DNSTechnicalCheckException;
import org.iana.dns.check.DNSTechnicalCheckExceptionVisitor;

/**
 * @author Piotr Tkaczyk
 */
public class InternalDNSCheckException extends DNSTechnicalCheckException {


    public InternalDNSCheckException() {
    }

    public InternalDNSCheckException(Throwable cause) {
        super(cause);
    }

    public void accept(DNSTechnicalCheckExceptionVisitor visitor) {
        //TODO
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
