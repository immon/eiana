package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Order {

    private String fieldName;
    private boolean ascending;

    public Order(String fieldName, boolean ascending) {
        this.fieldName = fieldName;
        this.ascending = ascending;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isAscending() {
        return ascending;
    }
}
