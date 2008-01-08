package org.iana.rzm.facade.system.notification;

import org.iana.criteria.*;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Jakub Laszkiewicz
 */
public class NotificationCriteriaConverter implements CriteriaVisitor {
    public void visitEqual(Equal crit) {
        if (crit.getValue() instanceof NotificationVO.Type)
            crit.setValue(NotificationConverter.toNotificationType((NotificationVO.Type) crit.getValue()));
    }

    public void visitIn(In crit) {
        Set<Object> values = new HashSet<Object>();
        for (Object o : crit.getValues())
            if (o instanceof NotificationVO.Type)
                values.add(NotificationConverter.toNotificationType((NotificationVO.Type) o));
            else
                values.add(o);
        crit.setValues(values);
    }

    public void visitGreater(Greater crit) {
        if (crit.getValue() instanceof NotificationVO.Type)
            crit.setValue(NotificationConverter.toNotificationType((NotificationVO.Type) crit.getValue()));
    }

    public void visitGreaterEqual(GreaterEqual crit) {
        if (crit.getValue() instanceof NotificationVO.Type)
            crit.setValue(NotificationConverter.toNotificationType((NotificationVO.Type) crit.getValue()));
    }

    public void visitLike(Like crit) {
    }

    public void visitLower(Lower crit) {
        if (crit.getValue() instanceof NotificationVO.Type)
            crit.setValue(NotificationConverter.toNotificationType((NotificationVO.Type) crit.getValue()));
    }

    public void visitLowerEqual(LowerEqual crit) {
        if (crit.getValue() instanceof NotificationVO.Type)
            crit.setValue(NotificationConverter.toNotificationType((NotificationVO.Type) crit.getValue()));
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
    }

    public void visitSort(SortCriterion sort) {
        sort.getCriterion().accept(this);
    }
}
