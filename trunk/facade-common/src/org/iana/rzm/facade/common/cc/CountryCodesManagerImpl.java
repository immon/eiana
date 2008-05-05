package org.iana.rzm.facade.common.cc;

import org.iana.codevalues.Code;
import org.iana.codevalues.CodeValuesManager;
import org.iana.codevalues.CodeValuesRetriever;
import org.iana.codevalues.Value;

/**
 * @author Patrycja Wegrzynowicz
 */
public class CountryCodesManagerImpl extends CountryCodesImpl implements CountryCodesManager {

    private CodeValuesManager manager;

    public CountryCodesManagerImpl(CodeValuesRetriever retriever, CodeValuesManager manager) {
        super(retriever);
        this.manager = manager;
    }

    public void addCountry(String countryCode, String countryName) throws CountryCodeAlreadyExistsException {
        String currentName = getCountryName(countryCode);
        if (currentName != null) throw new CountryCodeAlreadyExistsException("the code: " + countryCode + " is associated with the country: " + countryName);
        Code code = manager.getCode(CC);
        if (code == null) {
            code = new Code(CC);
            manager.create(code);
        }
        code.addValue(new Value(countryCode, countryName));
        manager.update(code);
        retriever.refresh();
    }

}
