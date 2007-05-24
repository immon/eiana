package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.CountryCode;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class AddressVO implements Serializable {

    private String textAddress;
    private CountryCode countryCode;

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
        this.countryCode = countryCode != null ? new CountryCode(countryCode) : null;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressVO addressVO = (AddressVO) o;

        if (countryCode != null ? !countryCode.equals(addressVO.countryCode) : addressVO.countryCode != null)
            return false;
        if (textAddress != null ? !textAddress.equals(addressVO.textAddress) : addressVO.textAddress != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (textAddress != null ? textAddress.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        return result;
    }
}
