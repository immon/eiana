package org.iana.ticketing.rt;

import org.iana.codevalues.CodeValuesRetriever;
import org.iana.codevalues.Value;
import org.iana.codevalues.Code;

import java.util.*;

/**
 * Mock implementation of CodeValuesRetriever interface to facilitate testing of RequestTrackerService.
 *
 * @author Patrycja Wegrzynowicz
 */
class CountryCodesRetriever implements CodeValuesRetriever {

    static Map<String, List<Value>> codes = new HashMap<String, List<Value>>();

    static {
        List<Value> cc = new ArrayList<Value>();
        cc.add(new Value("PL", "Poland"));
        cc.add(new Value("DE", "Germany"));
        cc.add(new Value("IE", "Ireland"));
        codes.put("cc", cc);
    }

    public List<Value> getCodeValues(String code) {
        List<Value> ret = codes.get(code);
        return ret == null ? Collections.EMPTY_LIST : ret;
    }

    public boolean hasValueId(String code, String id) {
        List<Value> vals = getCodeValues(code);
        for (Value val : vals)
            if (val.getValueId().equals(id)) return true;
        return false;
    }

    public boolean hasValue(String code, String value) {
        List<Value> vals = getCodeValues(code);
        for (Value val : vals)
            if (val.getValueName().equals(value)) return true;
        return false;
    }

    public String getValueById(String code, String id) {
        List<Value> vals = getCodeValues(code);
        for (Value val : vals)
            if (val.getValueId().equals(id)) return val.getValueName();
        return null;
    }

    public Code getCode(String code) {
        return null;
    }

    public void refresh() {

    }
}
