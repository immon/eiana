package org.iana.criteria;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Or extends MultipleCriterion {

    public Or(List<Criterion> criteria) {
        super(criteria);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitOr(this);
    }
}
