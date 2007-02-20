package org.iana.rzm.common.validators;

import org.iana.rzm.common.exceptions.InvalidIPv4AddressException;
import org.iana.rzm.common.exceptions.InvalidIPv6AddressException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IPAddressValidator {

    public void validateIPv4(String addr) throws InvalidIPv4AddressException {
        // todo
    }

    public void validateIPv6(String addr) throws InvalidIPv6AddressException {
        // todo
    }

    private IPAddressValidator() {
    }

    private static IPAddressValidator instance = new IPAddressValidator();

    public static IPAddressValidator getInstance() { return instance; }
}
