package org.iana.rzm.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Address implements Cloneable{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    private String textAddress;
    private String countryCode;
    private int id;

    public Address() {
    }

    public Address(String textAddress, String countryCode) {
        this.textAddress = textAddress;
        this.countryCode = countryCode;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getTextAddress() {
        return textAddress;
    }

    public void setTextAddress(String textAddress) {
        this.textAddress = textAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (countryCode != null ? !countryCode.equals(address.countryCode) : address.countryCode != null) return false;
        if (textAddress != null ? !textAddress.equals(address.textAddress) : address.textAddress != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (textAddress != null ? textAddress.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        return result;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
