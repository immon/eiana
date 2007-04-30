package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface CriteriaVisitor {

    public void visitEqual(Equal crit);

    public void visitIn(In crit);

    public void visitGreater(Greater crit);

    public void visitGreaterEqual(GreaterEqual crit);

    public void visitLike(Like crit);

    public void visitLower(Lower crit);

    public void visitLowerEqual(LowerEqual crit);

    public void visitNot(Not crit);

    public void visitAnd(And crit);

    public void visitOr(Or crit);

    public void visitIsNull(IsNull crit);
}
