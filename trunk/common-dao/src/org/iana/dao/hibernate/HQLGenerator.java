package org.iana.dao.hibernate;

import org.iana.criteria.*;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HQLGenerator implements CriteriaVisitor {

    private HQLBuffer buf = new HQLBuffer();

    public static HQLBuffer from(Class clazz, Criterion where) {
        HQLBuffer buf = new HQLBuffer();
        buf.append("from").append(clazz.getName());
        if (where != null) buf.append("where").append(toHQL(where));
        return buf;
    }

    public static HQLBuffer toHQL(Criterion crit) {
        HQLGenerator gen = new HQLGenerator();
        crit.accept(gen);
        return gen.buf;
    }

    public void visitEqual(Equal crit) {
        buf.op("=", crit.getFieldName(), crit.getValue());
    }

    public void visitIn(In crit) {
        buf.in(crit.getFieldName(), crit.getValues());
    }

    public void visitGreater(Greater crit) {
        buf.op(">", crit.getFieldName(), crit.getValue());
    }

    public void visitGreaterEqual(GreaterEqual crit) {
        buf.op(">=", crit.getFieldName(), crit.getValue());
    }

    public void visitLike(Like crit) {
        buf.op("like", crit.getFieldName(), crit.getValue());
    }

    public void visitLower(Lower crit) {
        buf.op("<", crit.getFieldName(), crit.getValue());
    }

    public void visitLowerEqual(LowerEqual crit) {
        buf.op("<=", crit.getFieldName(), crit.getValue());
    }

    public void visitNot(Not crit) {
        buf.append("not").append(toHQL(crit.getArg()));
    }

    public void visitAnd(And crit) {
        visitMultipleCriterion("and", crit);
    }

    public void visitOr(Or crit) {
        visitMultipleCriterion("or", crit);
    }

    private void visitMultipleCriterion(String op, MultipleCriterion crit) {
        boolean first = true;
        for (Criterion arg : crit.getArgs()) {
            if (!first) buf.append(op);
            else first = false;
            buf.append(toHQL(arg));
        }
    }

    public void visitIsNull(IsNull crit) {
        buf.op("is null", crit.getFieldName());
    }
}
