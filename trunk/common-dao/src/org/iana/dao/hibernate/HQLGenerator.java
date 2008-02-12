package org.iana.dao.hibernate;

import org.iana.criteria.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HQLGenerator implements CriteriaVisitor {

    private HQLBuffer where = new HQLBuffer();
    private HQLBuffer order = new HQLBuffer();

    public HQLGenerator() {
    }

    public HQLGenerator(String prefix) {
        where.setPrefix(prefix);
        order.setPrefix(prefix);
    }

    public HQLBuffer select(String select, String from, Criterion criteria) {
        HQLBuffer ret = new HQLBuffer();
        ret.append(select).appendSimple(from(from, criteria));
        return ret;
    }

    public HQLBuffer select(String select, Class clazz, Criterion criteria) {
        HQLBuffer ret = new HQLBuffer();
        ret.append(select).appendSimple(from(clazz, criteria));
        return ret;
    }

    public HQLBuffer from(Class clazz, Criterion criteria) {
        return from(clazz.getName(), criteria);
    }

    public HQLBuffer from(String from, Criterion criteria) {
        HQLBuffer buf = new HQLBuffer();
        buf.append("from").append(from);

        if (criteria != null) {
            criteria.accept(this);
            if (!where.isEmpty()) buf.append("where").appendSimple(where);
            if (!order.isEmpty()) buf.append("order by").appendSimple(order);
        }

        return buf;
    }

    public HQLGenerator toHQL(Criterion crit) {
        crit.accept(this);
        return this;
    }

    public void visitEqual(Equal crit) {
        where.op("=", getFieldName(crit), getValue(crit));
    }

    public void visitIn(In crit) {
        where.in(getFieldName(crit), getValues(crit));
    }

    public void visitGreater(Greater crit) {
        where.op(">", getFieldName(crit), getValue(crit));
    }

    public void visitGreaterEqual(GreaterEqual crit) {
        where.op(">=", getFieldName(crit), getValue(crit));
    }

    public void visitLike(Like crit) {
        where.op("like", getFieldName(crit), getValue(crit));
    }

    public void visitLower(Lower crit) {
        where.op("<", getFieldName(crit), getValue(crit));
    }

    public void visitLowerEqual(LowerEqual crit) {
        where.op("<=", getFieldName(crit), getValue(crit));
    }

    public void visitNot(Not crit) {
        where.append("not");
        where.append("(");
        crit.getArg().accept(this);
        where.append(")");
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
            if (!first) where.append(op);
            else first = false;
            where.append("(");
            arg.accept(this);
            where.append(")");
        }
    }

    public void visitIsNull(IsNull crit) {
        where.op("is null", getFieldName(crit));
    }

    public void visitSort(SortCriterion sort) {
        if (sort.getCriterion() != null) sort.getCriterion().accept(this);
        if (sort.getOrders() != null) {
            for (Order order : sort.getOrders()) {
                this.order.order(order.getFieldName(), order.isAscending());
            }
        }
    }

    protected String getFieldName(FieldCriterion crit) {
        return crit.getFieldName();
    }

    protected Object getValue(ValuedFieldCriterion crit) {
        return crit.getValue();
    }

    protected Set<? extends Object> getValues(In crit) {
        return crit.getValues();
    }

    private void append(HQLGenerator gen) {
        this.where.append(gen.where);
        this.order.append(gen.order);
    }

    private void appendSimple(HQLGenerator gen) {
        this.where.appendSimple(gen.where);
        this.order.appendSimple(gen.order);
    }
}
