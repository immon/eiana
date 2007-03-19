package org.iana.rzm.domain;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;
import org.iana.rzm.common.validators.IPAddressValidator;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class IPv6Address extends IPAddress {

    protected IPv6Address() {}

    IPv6Address(String address) throws InvalidIPAddressException {
        super(address, Type.IPv6);
        isValidAddress(address);
    }

    @Transient
    private void isValidAddress(String address) throws InvalidIPAddressException {
        IPAddressValidator.getInstance().validateIPv6(address);
    }
}
