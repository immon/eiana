package org.iana.criteria;

import org.iana.rzm.common.validators.CheckTool;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/**
 * @author Patrycja Wegrzynowicz
 */
public class And extends MultipleCriterion {

    public And(Criterion arg1, Criterion arg2) {
        super(Arrays.asList(arg1, arg2));
    }

    public And(List<Criterion> criteria) {
        super(criteria);
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitAnd(this);
    }
}
