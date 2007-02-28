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
public class Address {

    private Long objId;
    private String street;
    private String city;
    private String postalCode;
    private String countryCode;
    private String state;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (city != null ? !city.equals(address.city) : address.city != null) return false;
        if (countryCode != null ? !countryCode.equals(address.countryCode) : address.countryCode != null) return false;
        if (postalCode != null ? !postalCode.equals(address.postalCode) : address.postalCode != null) return false;
        if (state != null ? !state.equals(address.state) : address.state != null) return false;
        if (street != null ? !street.equals(address.street) : address.street != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (street != null ? street.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
