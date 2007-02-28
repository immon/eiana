package org.iana.rzm.domain;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidIPv4AddressException;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class IPv4Address extends IPAddress {

    protected IPv4Address() {}

    IPv4Address(String address) throws InvalidIPAddressException {
        super(address, Type.IPv4);
        isValidAddress(address);
    }

    @Transient
    private void isValidAddress(String address) throws InvalidIPAddressException {
        // todo: delegate to IP validator
        if (address == null) throw new NullPointerException("null ipv4 address");
        String[] tokens = address.split("\\.");
        if (tokens.length != 4) throw new InvalidIPv4AddressException(address);
        for (String token : tokens) {
            try {
                int num = Integer.parseInt(token);
                if (num < 0 || num > 255 || !Integer.toString(num).equals(token)) throw new InvalidIPv4AddressException(address);

            } catch (NumberFormatException e) {
                throw new InvalidIPv4AddressException(address);
            }
        }
    }
}
