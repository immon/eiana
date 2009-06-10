package org.iana.rzm.web.model.criteria;

import java.io.*;

public class SortOrder implements Serializable {

    private String fieldName;
    private boolean isAscending;

    public SortOrder() {
    }

    public SortOrder(String fieldName, boolean ascending) {
        this.fieldName = fieldName;
        isAscending = ascending;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isAscending() {
        return isAscending;
    }

    public void setAscending(boolean ascending) {
        isAscending = ascending;
    }

    public boolean isValid() {
        return getFieldName() != null;
    }
}
