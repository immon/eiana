package org.iana.rzm.common.exceptions;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InvalidIPv6AddressException extends InvalidIPAddressException {

    public InvalidIPv6AddressException(String address) {
        super(address);
    }
}
