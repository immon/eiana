package org.iana.rzm.web.tapestry.editors;

import org.iana.codevalues.Value;

import java.util.List;
import java.util.Map;


public interface ContactAttributesEditor extends AttributesEditor {
    public void save(Map<String, String> attributes);
    public List<Value> getCountrys();
    public boolean isValidCountryCode(String code);
}
