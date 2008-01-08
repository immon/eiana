package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Equal extends ValuedFieldCriterion {

    public Equal(String fieldName, Object value) {
        super(fieldName, value);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitEqual(this);
    }
}
