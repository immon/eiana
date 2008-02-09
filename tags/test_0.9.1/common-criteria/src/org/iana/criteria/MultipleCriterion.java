package org.iana.criteria;

import org.iana.rzm.common.validators.CheckTool;

import java.util.List;
import java.util.Collections;

/**
 * @author Patrycja Wegrzynowicz
 */
abstract public class MultipleCriterion implements Criterion {

    private List<Criterion> args;

    protected MultipleCriterion(List<Criterion> criteria) {
        CheckTool.checkCollectionNullOrEmpty(criteria, "criteria arguments");
        this.args = criteria;
    }

    public List<Criterion> getArgs() {
        return Collections.unmodifiableList(args);
    }
}
