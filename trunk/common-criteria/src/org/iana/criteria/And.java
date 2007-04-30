package org.iana.criteria;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class And extends MultipleCriterion {

    public And(List<Criterion> criteria) {
        super(criteria);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitAnd(this);
    }
}
