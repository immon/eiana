package org.iana.criteria;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Order implements Serializable {

    private String fieldName;
    private Boolean ascending;

    public Order(String fieldName) {
        this(fieldName, true);    
    }

    public Order(String fieldName, boolean ascending) {
        this.fieldName = fieldName;
        this.ascending = ascending;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isAscending() {
        return ascending;
    }
}
