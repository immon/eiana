package org.iana.rzm.domain;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;

public class IPv6Address extends IPAddress {

    IPv6Address(String address) throws InvalidIPAddressException {
        super(address, Type.IPv6);
        isValidAddress(address);
    }

    private void isValidAddress(String address) throws InvalidIPAddressException {
        // todo: delegate to IP validator
    }
}
