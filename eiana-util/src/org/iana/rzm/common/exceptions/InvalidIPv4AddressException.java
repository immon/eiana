package org.iana.rzm.common.exceptions;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InvalidIPv4AddressException extends InvalidIPAddressException {

    public InvalidIPv4AddressException(String address) {
        super(address);
    }
}
