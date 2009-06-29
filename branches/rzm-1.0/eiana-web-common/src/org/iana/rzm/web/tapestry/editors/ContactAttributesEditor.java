package org.iana.rzm.web.tapestry.editors;

import org.iana.codevalues.*;
import org.iana.rzm.web.tapestry.editors.AttributesEditor;

import java.util.*;


public interface ContactAttributesEditor extends AttributesEditor {
    public void save(Map<String, String> attributes);
    public List<Value> getCountrys();
    public boolean isValidCountryCode(String code);
}
