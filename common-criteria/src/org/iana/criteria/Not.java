package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Not implements Criterion {

    private Criterion arg;

    public Not(Criterion arg) {
        this.arg = arg;
    }

    public Criterion getArg() {
        return arg;
    }

    public void accept(CriteriaVisitor visitor) {
        visitor.visitNot(this);
    }
}
