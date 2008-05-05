package org.iana.rzm.facade.common.cc;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface CountryCodesManager extends CountryCodes {

    public void addCountry(String countryCode, String countryName) throws CountryCodeAlreadyExistsException;

}
