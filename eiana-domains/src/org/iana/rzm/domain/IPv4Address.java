package org.iana.rzm.domain;

import org.iana.dns.DNSIPv4Address;
import org.iana.dns.obj.DNSIPAddressImpl;
import org.iana.dns.validator.InvalidIPAddressException;

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

    protected String normalizeAddress(String address) throws InvalidIPAddressException {
        DNSIPv4Address ipv4 = DNSIPAddressImpl.createIPv4Address(address);
        return ipv4.getAddress();
    }
}
