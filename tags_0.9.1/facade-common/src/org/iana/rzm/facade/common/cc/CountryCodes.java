package org.iana.rzm.facade.common.cc;

import org.iana.codevalues.Value;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface CountryCodes {

    String getCountryName(String countryCode);

    List<Value> getCountries();
}
