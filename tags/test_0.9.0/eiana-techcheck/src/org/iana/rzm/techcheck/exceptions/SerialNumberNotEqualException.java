package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */
public class SerialNumberNotEqualException extends DomainException {


    public SerialNumberNotEqualException(String hostName, String serial) {
        super(hostName, serial);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
