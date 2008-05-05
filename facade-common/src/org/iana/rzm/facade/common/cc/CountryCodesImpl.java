package org.iana.rzm.facade.common.cc;

import org.iana.codevalues.Value;
import org.iana.codevalues.CodeValuesRetriever;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class CountryCodesImpl implements CountryCodes {

    /**
     * The name of the code containing country information (country code and name).
     */
    public static final String CC = "cc";

    protected CodeValuesRetriever retriever;

    public CountryCodesImpl(CodeValuesRetriever retriever) {
        if (retriever == null) throw new IllegalArgumentException("null code values retriever");
        this.retriever = retriever;
    }

    public String getCountryName(String countryCode) {
        return retriever.getValueById(CC, countryCode);
    }

    public List<Value> getCountries() {
        return retriever.getCodeValues(CC);
    }
}
