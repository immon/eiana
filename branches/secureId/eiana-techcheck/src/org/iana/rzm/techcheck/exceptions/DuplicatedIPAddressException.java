package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class DuplicatedIPAddressException extends DomainException {

    public DuplicatedIPAddressException(String hostName, String ipAddress) {
        super(hostName, ipAddress);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
