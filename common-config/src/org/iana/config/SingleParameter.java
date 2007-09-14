package org.iana.config;

import javax.persistence.Basic;
import javax.persistence.Entity;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.trim().length() == 0)
            throw new IllegalArgumentException("parameter value cannot be null or empty");
        this.value = value;
    }
}
