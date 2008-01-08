package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class LowerEqual extends ValuedFieldCriterion {

    public LowerEqual(String fieldName, Object value) {
        super(fieldName, value);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitLowerEqual(this);
    }
}
