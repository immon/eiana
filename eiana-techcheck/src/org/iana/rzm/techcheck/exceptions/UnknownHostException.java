package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class UnknownHostException extends DomainException {


    public UnknownHostException(String hostName) {
        super(hostName, "");
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
