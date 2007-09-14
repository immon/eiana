package org.iana.config;

import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

/**
 * It represents a list parameter stored in a config.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */

@Entity
public class ListParameter extends AbstractParameter {

    /**
     * The list of values of the parameter. Cannot be null
     * and cannot store null values.
     */
    @CollectionOfElements(fetch = FetchType.LAZY)
    private List<String> values = new ArrayList<String>();

    private ListParameter() {
    }

    public ListParameter(String name, List<String> values) {
        setName(name);
        setValues(values);
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        if (values == null) throw new IllegalArgumentException("parameter value cannot be null");
        for (String s : values)
            if (s == null || s.trim().length() == 0)
                throw new IllegalArgumentException("list values cannot be null or empty");
        this.values = values;
    }
}
