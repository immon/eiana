package org.iana.dns.obj;

import org.iana.dns.DNSIPAddress;
import org.iana.dns.DNSIPv6Address;
import org.iana.dns.DNSIPv4Address;
import org.iana.dns.validator.InvalidIPAddressException;

/**
 * @author Patrycja Wegrzynowicz
 */
public abstract class DNSIPAddressImpl implements DNSIPAddress {

    protected String address;

    public DNSIPAddressImpl(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int[] getInts() {
        String[] parts = getParts();
        int[] ret = new int[parts.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = toInt(parts[i]);
        }
        return ret;
    }

    abstract protected int toInt(String s);

    public static DNSIPAddress createIPAddress(String addr) throws InvalidIPAddressException {
        try {
            return createIPv4Address(addr);
        } catch (InvalidIPAddressException e) {
            return createIPv6Address(addr);
        }
    }

    public static DNSIPv4Address createIPv4Address(String addr) throws InvalidIPAddressException {
        return new DNSIPv4AddressImpl(addr);
    }

    public static DNSIPv6Address createIPv6Address(String addr) throws InvalidIPAddressException {
        return new DNSIPv6AddressImpl(addr);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNSIPAddressImpl that = (DNSIPAddressImpl) o;

        if (address != null ? !address.equals(that.address) : that.address != null) return false;

        return true;
    }

    public int hashCode() {
        return (address != null ? address.hashCode() : 0);
    }
}
