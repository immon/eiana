package org.iana.rzm.domain;

import org.iana.rzm.common.exceptions.InvalidIPAddressException;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public abstract class IPAddress {

    public static enum Type { IPv4, IPv6 }

    private Long objId;
    private String address;
    private Type type;

    protected IPAddress() {}
    
    protected IPAddress(String address, Type type) {
        this.address = address;
        this.type = type;
    }

    @Transient
    final public boolean isIPv4() { return Type.IPv4 == type; }

    @Transient
    final public boolean isIPv6() { return Type.IPv6 == type; }

    @Transient
    final public Type getType() { return type; }

    @Column(name = "type")
    protected Type getIPAddressType() { return type; }

    protected void setIPAddressType(Type type) { this.type = type; }

    @Transient
    final public String getAddress() { return address; }

    @Column(name = "address")
    protected String getIPAddressAddress() { return address; }

    protected void setIPAddressAddress(String address) { this.address = address; }

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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPAddress ipAddress = (IPAddress) o;

        if (address != null ? !address.equals(ipAddress.address) : ipAddress.address != null) return false;
        if (type != ipAddress.type) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (address != null ? address.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
