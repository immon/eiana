package org.iana.criteria;

import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
abstract class FieldCriterion implements Criterion {

    private String fieldName;

    protected FieldCriterion(String fieldName) {
        CheckTool.checkNull(fieldName, "null field name");
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
