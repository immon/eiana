package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Criterion {

    public void accept(CriteriaVisitor visitor);
}
