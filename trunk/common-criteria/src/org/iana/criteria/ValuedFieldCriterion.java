package org.iana.criteria;

import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public abstract class ValuedFieldCriterion extends FieldCriterion {

    private Object value;

    public ValuedFieldCriterion(String fieldName, Object value) {
        super(fieldName);
        CheckTool.checkNull(value, "null field value");
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
