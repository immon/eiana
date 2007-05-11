package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class DuplicatedIPAddressException extends DomainValidationException {
    String ipAddress;

    public DuplicatedIPAddressException(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }
}
