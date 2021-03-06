package org.iana.rzm.domain;

import org.iana.rzm.common.CountryCode;
import org.iana.rzm.common.exceptions.InvalidCountryCodeException;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Embeddable
public class Address implements Cloneable{

    @Basic 
    @Column(length = 4096)
    private String textAddress;
    @Embedded
    private CountryCode countryCode;
    @Basic
    @Column(length = 4096)
    private String country;

    public Address() {
    }

    public Address(String textAddress, String countryCode) throws InvalidCountryCodeException {
        this.textAddress = textAddress;
        setCountryCode(countryCode);
    }

    public String getTextAddress() {
        return textAddress;
    }

    public void setTextAddress(String textAddress) {
        System.out.println(textAddress);
        if (textAddress != null && textAddress.length() > 4000) {
            System.out.println(textAddress);
            textAddress = textAddress.substring(0, 4000);
        }
        this.textAddress = textAddress;
    }

    public String getCountryCode() {
        return countryCode == null ? null : countryCode.getCountryCode();
    }

    public void setCountryCode(String countryCode) throws InvalidCountryCodeException {
        this.countryCode = countryCode != null ? new CountryCode(countryCode) : null;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (country != null ? !country.equals(address.country) : address.country != null) return false;
        if (countryCode != null ? !countryCode.equals(address.countryCode) : address.countryCode != null) return false;
        if (textAddress != null ? !textAddress.equals(address.textAddress) : address.textAddress != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (textAddress != null ? textAddress.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    protected Address clone() {
        try {
            return (Address) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException();
        }
    }
}
