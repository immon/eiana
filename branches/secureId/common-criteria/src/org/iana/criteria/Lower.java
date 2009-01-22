package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Lower extends ValuedFieldCriterion {

    public Lower(String fieldName, Object value) {
        super(fieldName, value);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitLower(this);
    }
}
