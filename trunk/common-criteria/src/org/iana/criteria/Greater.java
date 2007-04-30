package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Greater extends ValuedFieldCriterion {

    public Greater(String fieldName, Object value) {
        super(fieldName, value);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitGreater(this);
    }
}
