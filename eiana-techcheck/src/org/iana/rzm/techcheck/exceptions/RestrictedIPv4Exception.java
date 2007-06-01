package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class RestrictedIPv4Exception extends DomainException {


    public RestrictedIPv4Exception(String ipAddress) {
        super("", ipAddress);
    }

    public RestrictedIPv4Exception(String hostName, String ipAddress) {
        super(hostName, ipAddress);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
