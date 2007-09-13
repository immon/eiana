package org.iana.criteria;

import java.util.ArrayList;
import java.util.List;

/**
 * It represents a parameter stored in a config.
 *
 * @author Patrycja Wegrzynowicz
 */
public class Parameter {

    /**
     * The name of the parameter. Cannot be null.
     */
    private String name;

    /**
     * The list of values of the parameter. Cannot be null
     * and cannot store null values.
     *
     * // todo: to consider whether to relax null-values policy (usefullness of null values)
     */
    private List<String> values = new ArrayList<String>();
    
}
