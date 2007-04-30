package org.iana.criteria;

import org.iana.rzm.common.validators.CheckTool;

import java.util.Set;
import java.util.Collections;

/**
 * @author Patrycja Wegrzynowicz
 */
public class In extends FieldCriterion {

    private Set<Object> values;

    public In(String fieldName, Set<Object> values) {
        super(fieldName);
        CheckTool.checkCollectionNull(values, "null in criterion values");
        this.values = values;
    }

    public Set<Object> getValues() {
        return Collections.unmodifiableSet(values);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitIn(this);
    }
}
