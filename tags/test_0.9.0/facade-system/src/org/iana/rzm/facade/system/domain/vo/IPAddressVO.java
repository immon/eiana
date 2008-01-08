package org.iana.rzm.facade.system.domain.vo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IPAddressVO implements Serializable {

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

    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
