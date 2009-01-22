package org.iana.criteria;

import org.iana.rzm.common.validators.CheckTool;

import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
abstract public class FieldCriterion implements Criterion, Serializable {

    private String fieldName;

    protected FieldCriterion(String fieldName) {
        CheckTool.checkNull(fieldName, "null field name");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
