package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class GreaterEqual extends ValuedFieldCriterion {

    public GreaterEqual(String fieldName, Object value) {
        super(fieldName, value);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitGreaterEqual(this);
    }
}
