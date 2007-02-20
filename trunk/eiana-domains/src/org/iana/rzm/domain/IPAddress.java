package org.iana.rzm.domain;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;

public abstract class IPAddress {

    public static enum Type { IPv4, IPv6 };

    private String address;
    private Type type;

    protected IPAddress(String address, Type type) {
        this.address = address;
        this.type = type;
    }

    final public boolean isIPv4() { return Type.IPv4 == type; }

    final public boolean isIPv6() { return Type.IPv6 == type; }

    final public Type getType() { return type; }

    final public String getAddress() { return address; }

    public static IPAddress createIPAddress(String addr) throws InvalidIPAddressException {
        try {
            return createIPv4Address(addr);
        } catch (InvalidIPAddressException e) {
            return createIPv6Address(addr);
        }
    }

    public static IPv4Address createIPv4Address(String addr) throws InvalidIPAddressException {
        return new IPv4Address(addr);
    }

    public static IPv6Address createIPv6Address(String addr) throws InvalidIPAddressException {
        return new IPv6Address(addr);
    }
}
