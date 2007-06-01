package org.iana.rzm.domain;

import org.iana.dns.validator.InvalidIPAddressException;
import org.iana.dns.validator.IPAddressValidator;

import javax.persistence.Entity;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class IPv4Address extends IPAddress {

    protected IPv4Address() {}

    IPv4Address(String address) throws InvalidIPAddressException {
        super(address, Type.IPv4);
        setAddress(address);
    }

    protected void isValidAddress(String address) throws InvalidIPAddressException {
        IPAddressValidator.getInstance().validateIPv4(address);
    }
}
