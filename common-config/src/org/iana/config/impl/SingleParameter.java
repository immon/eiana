package org.iana.config.impl;

import javax.persistence.Basic;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * It represents a single parameter stored in a config.
 *
 * @author Piotr Tkaczyk
 */

@Entity
public class SingleParameter extends AbstractParameter {

    /**
     * Value of parameter cannot be null;
     */
    @Basic
    private String value;

    private SingleParameter() {
    }

    public SingleParameter(String name, String value) {
        setValue(value);
        setName(name);
    }

    public void setValue(String value) {
        if (value == null || value.trim().length() == 0)
            throw new IllegalArgumentException("parameter value cannot be null or empty");
        this.value = value;
    }


    public String getParameter() {
        return value;
    }

    public List<String> getParameterList() {
        List<String> ret = new ArrayList<String>();
        ret.add(value);
        return ret;
    }

    public Set<String> getParameterSet() {
        Set<String> ret = new HashSet<String>();
        ret.add(value);
        return ret;
    }
}
