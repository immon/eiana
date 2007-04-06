package org.iana.rzm.domain;

import org.iana.rzm.common.CountryCode;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class Address implements Cloneable{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private String textAddress;
    @Embedded
    private CountryCode countryCode;
    @Basic
    private int id;

    public Address() {
    }

    public Address(String textAddress, String countryCode) {
        this.textAddress = textAddress;
        this.countryCode = new CountryCode(countryCode);
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
        return countryCode == null ? null : countryCode.getCountryCode();
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = new CountryCode(countryCode);
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
