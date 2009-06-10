package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IsNull extends FieldCriterion {

    public IsNull(String fieldName) {
        super(fieldName);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitIsNull(this);
    }
}
