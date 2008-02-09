package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class NoAuthoritativeNameServerException extends DomainException {

    public NoAuthoritativeNameServerException(String hostName) {
        super(hostName, "");
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
