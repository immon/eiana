package org.iana.criteria;

import org.iana.rzm.common.validators.CheckTool;

import java.util.Collections;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class In extends FieldCriterion {

    private Set<? extends Object> values;

    public In(String fieldName, Set<? extends Object> values) {
        super(fieldName);
        CheckTool.checkCollectionNull(values, "null in criterion values");
        CheckTool.checkCollectionEmpty(values, "in criterion values");
        this.values = values;
    }

    public Set<? extends Object> getValues() {
        return Collections.unmodifiableSet(values);
    }

    public void setValues(Set<? extends Object> values) {
        this.values = values;
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitIn(this);
    }
}
