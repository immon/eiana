package org.iana.rzm.web.common.utils;

import org.iana.codevalues.*;

import java.util.*;

public class CountryCodeSorter implements Comparator<Value> {

    public int compare(Value o1, Value o2) {
        return o1.getValueName().compareTo(o2.getValueName());
    }
}
