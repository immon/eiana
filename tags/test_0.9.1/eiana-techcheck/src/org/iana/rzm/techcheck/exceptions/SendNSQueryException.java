package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class SendNSQueryException extends DomainException {

    public SendNSQueryException(String hostName, String value) {
        super(hostName, value);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
