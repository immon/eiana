package org.iana.config;

import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

/**
 * It represents a set parameter stored in a config.
 *
 * @author Piotr Tkaczyk
 */

@Entity
public class SetParameter extends AbstractParameter {

    /**
     * The set of values of the parameter. Cannot be null
     * and cannot store null values.
     */
    @CollectionOfElements(fetch = FetchType.EAGER)
    private Set<String> values = new HashSet<String>();

    private SetParameter() {
    }

    public SetParameter(String name, Set<String> values) {
        setName(name);
        setValues(values);
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        if (values == null) throw new IllegalArgumentException("parameter value cannot be null");
        for (String s : values)
            if (s == null || s.trim().length() == 0)
                throw new IllegalArgumentException("list values cannot be null or empty");
        this.values = values;
    }
}
