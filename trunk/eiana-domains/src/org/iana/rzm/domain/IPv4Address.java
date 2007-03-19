package org.iana.rzm.domain;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.exceptions.InvalidIPv4AddressException;
import org.iana.rzm.common.validators.IPAddressValidator;

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
        IPAddressValidator.getInstance().validateIPv4(address);
    }
}
