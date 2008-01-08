package org.iana.criteria;

/**
 * @author Patrycja Wegrzynowicz
 */
public abstract class FieldVisitor implements CriteriaVisitor {

    public void visitEqual(Equal crit) {
        visitField(crit);
    }

    public void visitIn(In crit) {
        visitField(crit);
    }

    public void visitGreater(Greater crit) {
        visitField(crit);
    }

    public void visitGreaterEqual(GreaterEqual crit) {
        visitField(crit);
    }

    public void visitLike(Like crit) {
        visitField(crit);
    }

    public void visitLower(Lower crit) {
        visitField(crit);
    }

    public void visitLowerEqual(LowerEqual crit) {
        visitField(crit);
    }

    public void visitNot(Not crit) {
        crit.getArg().accept(this);
    }

    public void visitAnd(And crit) {
        for (Criterion c : crit.getArgs())
            c.accept(this);
    }

    public void visitOr(Or crit) {
        for (Criterion c : crit.getArgs())
            c.accept(this);
    }

    public void visitIsNull(IsNull crit) {
        visitField(crit);
    }

    public void visitSort(SortCriterion sort) {
        Criterion crit = sort.getCriterion();
        if (crit != null) crit.accept(this);
    }

    protected abstract void visitField(FieldCriterion field);
}
