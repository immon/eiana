package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Like extends ValuedFieldCriterion {

    public Like(String fieldName, String value) {
        super(fieldName, value);
    }

    public String getValue() {
        return (String) super.getValue();
    }    

    public void accept(CriteriaVisitor visitor) {
        visitor.visitLike(this);
    }
}
