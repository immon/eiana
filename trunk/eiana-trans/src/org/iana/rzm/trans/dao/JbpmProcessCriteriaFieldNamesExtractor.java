package org.iana.rzm.trans.dao;

import org.iana.criteria.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
class JbpmProcessCriteriaFieldNamesExtractor implements CriteriaVisitor {
    private Set<String> names = new HashSet<String>();
    private Map<String, String> fieldNames = new HashMap<String, String>();


    public JbpmProcessCriteriaFieldNamesExtractor(Map<String, String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public void visitEqual(Equal crit) {
        replaceFieldName(crit);
    }

    public void visitIn(In crit) {
        replaceFieldName(crit);
    }

    public void visitGreater(Greater crit) {
        replaceFieldName(crit);
    }

    public void visitGreaterEqual(GreaterEqual crit) {
        replaceFieldName(crit);
    }

    public void visitLike(Like crit) {
        replaceFieldName(crit);
    }

    public void visitLower(Lower crit) {
        replaceFieldName(crit);
    }

    public void visitLowerEqual(LowerEqual crit) {
        replaceFieldName(crit);
    }

    public void visitNot(Not crit) {
        crit.getArg().accept(this);
    }

    public void visitAnd(And crit) {
        for (Criterion sub : crit.getArgs())
            sub.accept(this);
    }

    public void visitOr(Or crit) {
        for (Criterion sub : crit.getArgs())
            sub.accept(this);
    }

    public void visitIsNull(IsNull crit) {
        replaceFieldName(crit);
    }

    public void visitSort(SortCriterion sort) {
        if (sort.getCriterion() != null)
            sort.getCriterion().accept(this);
        if (sort.getOrders() != null) {
            for (Order order : sort.getOrders())
                replaceFieldName(order);
        }
    }

    public Set<String> getNames() {
        return names;
    }

    private void replaceFieldName(FieldCriterion crit) {
        crit.setFieldName(replaceName(crit.getFieldName()));
    }

    private void replaceFieldName(Order order) {
        order.setFieldName(replaceName(order.getFieldName()));
    }

    private String replaceName(String name) {
        String[] elems = name.split("\\.", 2);
        name = elems[0];
        String newName = fieldNames.get(name);
        if (newName == null) newName = name;
        if (elems.length > 1) newName += "." + elems[1];
        names.add(newName);
        return newName;
    }
}
