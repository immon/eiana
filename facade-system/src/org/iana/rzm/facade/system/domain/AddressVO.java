package org.iana.rzm.facade.system.domain;

import org.iana.rzm.common.CountryCode;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AddressVO {

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
        this.countryCode = new CountryCode(countryCode);
    }
}
