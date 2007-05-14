package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class DuplicatedIPAddressException extends DomainCheckException {
    String ipAddress;

    public DuplicatedIPAddressException(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }
}
