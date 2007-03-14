package org.iana.rzm.facade.system.domain;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IPAddressVO {

    public enum Type { IPv4, IPv6 }

    private String address;
    private Type type;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
