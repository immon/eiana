package org.iana.codevalues;

import java.util.List;

/**
 * It provides a method to retrieve values associated with a given code. It forms a sort
 * of an application dictionary which can be used to populate, for example, combo boxes in user interface.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface CodeValuesRetriever {

    public List<Value> getCodeValues(String code);
}
