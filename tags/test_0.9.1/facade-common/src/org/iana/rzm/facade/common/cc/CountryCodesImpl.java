package org.iana.rzm.facade.common.cc;

import org.iana.codevalues.Value;
import org.iana.codevalues.CodeValuesRetriever;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class CountryCodesImpl implements CountryCodes {

    private CodeValuesRetriever retriever;

    public CountryCodesImpl(CodeValuesRetriever retriever) {
        if (retriever == null) throw new IllegalArgumentException("null code values retriever");
        this.retriever = retriever;
    }

    public String getCountryName(String countryCode) {
        return retriever.getValueById("cc", countryCode);
    }

    public List<Value> getCountries() {
        return retriever.getCodeValues("cc");
    }
}
