package org.iana.dns.validator;

/**
 * @author Patrycja Wegrzynowicz
 */
public class InvalidIPv6AddressException extends InvalidIPAddressException {

    public InvalidIPv6AddressException(String address) {
        super(address);
    }
}
