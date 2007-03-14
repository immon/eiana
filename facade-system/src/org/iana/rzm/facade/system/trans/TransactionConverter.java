package org.iana.rzm.facade.system.trans;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionAction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.change.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Converts a domain transaction business and persistent object to a domain transaction
 * value object. Value objects are detached from a persistent store
 *
 * @author Patrycja Wegrzynowicz
 */
class TransactionConverter {

    public static TransactionVO toTransactionVO(final Transaction trans) {
        if (trans == null) return null;

        TransactionVO ret = new TransactionVO();
        ret.setTransactionID(trans.getTransactionID());
        ret.setTicketID(trans.getTicketID());
        ret.setName(trans.getName());
        ret.setDomainName(trans.getCurrentDomain().getName());

        List<TransactionActionVO> actions = new ArrayList<TransactionActionVO>();
        for (TransactionAction action : trans.getActions()) {
            if (action != null) {
                actions.add(toActionVO(action));
            }
        }
        ret.setDomainActions(actions);
        ret.setState(toStateVO(trans.getState()));
        ret.setStart(trans.getStart());
        ret.setEnd(trans.getEnd());

        return ret;
    }

    private static TransactionActionVO toActionVO(final TransactionAction action) {
        if (action == null) return null;

        TransactionActionVO ret = new TransactionActionVO();
        ret.setName(action.getName().toString());
        List<ChangeVO> changes = new ArrayList<ChangeVO>();
        for (Change change : action.getChange()) {
            if (change != null) {
                changes.add(toChangeVO(change));
            }
        }
        ret.setChange(changes);
        return ret;
    }

    private static class ChangeConverter implements ChangeVisitor {

        private ChangeVO ret;

        public ChangeConverter(ChangeVO ret) {
            this.ret = ret;
        }

        public void visitAddition(Addition add) {
            ret.setType(ChangeVO.Type.ADD);
            Value value = add.getValue();
            if (value != null) value.accept(new ValueConverter(ret));
        }

        public void visitRemoval(Removal rem) {
            ret.setType(ChangeVO.Type.REMOVE);
            Value value = rem.getValue();
            if (value != null) value.accept(new ValueConverter(ret));
        }

        public void visitModification(Modification mod) {
            ret.setType(ChangeVO.Type.UPDATE);
            Value value = mod.getValue();
            if (value != null) value.accept(new ValueConverter(ret));
        }
    }

    private static class ValueConverter implements ValueVisitor {

        private ChangeVO ret;

        public ValueConverter(ChangeVO ret) {
            this.ret = ret;
        }

        public void visitPrimitiveValue(PrimitiveValue value) {
            ret.setValue(new StringValueVO(value.getValue(), value.getValue()));
        }

        public void visitModifiedPrimitiveValue(ModifiedPrimitiveValue value) {
            ret.setValue(new StringValueVO(value.getOldValue(), value.getNewValue()));
        }

        public void visitObjectValue(ObjectValue value) {
            ObjectValueVO retValue = new ObjectValueVO(value.getId(), value.getName());
            List<ChangeVO> changes = new ArrayList<ChangeVO>();
            if (value.getChanges() != null) {
                for (Object object : value.getChanges()) {
                    Change change = (Change) object;
                    changes.add(toChangeVO(change));
                }
            }
            retValue.setChanges(changes);
            ret.setValue(retValue);
        }
    }

    private static ChangeVO toChangeVO(final Change change) {
        if (change == null) return null;

        ChangeVO ret = new ChangeVO();
        ret.setFieldName(change.getFieldName());
        change.accept(new ChangeConverter(ret));
        return ret;
    }

    private static TransactionStateVO toStateVO(final TransactionState state) {
        if (state == null) return null;

        TransactionStateVO ret = new TransactionStateVO();
        ret.setName(state.getName().toString());
        ret.setStart(state.getStart());
        ret.setEnd(state.getEnd());
        return ret;
    }
}
