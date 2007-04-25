package org.iana.rzm.common.exceptions;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InvalidIPAddressException extends RuntimeException {

    String address;

    public InvalidIPAddressException(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
