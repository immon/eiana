package org.iana.dao.hibernate;

import org.iana.criteria.*;

import java.util.Iterator;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class HQLGenerator implements CriteriaVisitor {

    private HQLBuffer where = new HQLBuffer();
    private HQLBuffer order = new HQLBuffer();

    public static HQLBuffer from(Class clazz, Criterion criteria) {
        return from(clazz.getName(), criteria);
    }

    public static HQLBuffer from(List<String> froms, Criterion criteria) {
        StringBuffer buff = new StringBuffer(" ");
        Iterator<String> iterator = froms.iterator();
        buff.append(iterator.next());
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (!line.startsWith("inner"))
                buff.append(", ");
            else
                buff.append(" ");
            buff.append(line);
        }
        buff.append(" ");
        return from(buff.toString(), criteria);
    }

    public static HQLBuffer from(String from, Criterion criteria) {
        HQLBuffer buf = new HQLBuffer();
        buf.append("from").append(from);

        if (criteria != null) {
            HQLGenerator gen = new HQLGenerator();
            criteria.accept(gen);
            if (gen.where.isEmpty()) buf.append("where").appendSimple(gen.where);
            if (gen.order.isEmpty()) buf.append("order by").appendSimple(gen.order);
        }

        return buf;
    }

    public static HQLGenerator toHQL(Criterion crit) {
        HQLGenerator gen = new HQLGenerator();
        crit.accept(gen);
        return gen;
    }

    public void visitEqual(Equal crit) {
        where.op("=", crit.getFieldName(), crit.getValue());
    }

    public void visitIn(In crit) {
        where.in(crit.getFieldName(), crit.getValues());
    }

    public void visitGreater(Greater crit) {
        where.op(">", crit.getFieldName(), crit.getValue());
    }

    public void visitGreaterEqual(GreaterEqual crit) {
        where.op(">=", crit.getFieldName(), crit.getValue());
    }

    public void visitLike(Like crit) {
        where.op("like", crit.getFieldName(), crit.getValue());
    }

    public void visitLower(Lower crit) {
        where.op("<", crit.getFieldName(), crit.getValue());
    }

    public void visitLowerEqual(LowerEqual crit) {
        where.op("<=", crit.getFieldName(), crit.getValue());
    }

    public void visitNot(Not crit) {
        where.append("not");
        append(toHQL(crit.getArg()));
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
            append(toHQL(arg));
        }
    }

    public void visitIsNull(IsNull crit) {
        where.op("is null", crit.getFieldName());
    }

    public void visitSort(SortCriterion sort) {
        if (sort.getCriterion() != null) appendSimple(toHQL(sort.getCriterion()));
        if (sort.getOrders() != null) {
            for (Order order : sort.getOrders()) {
                this.order.order(order.getFieldName(), order.isAscending());
            }
        }
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
