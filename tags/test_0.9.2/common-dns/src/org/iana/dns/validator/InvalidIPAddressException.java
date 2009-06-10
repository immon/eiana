package org.iana.dns.validator;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InvalidIPAddressException extends RuntimeException {

    private String address;

    public InvalidIPAddressException(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
