package org.iana.rzm.facade.system.domain;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AddressVO {

    private String textAddress;
    private String countryCode;

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
}
