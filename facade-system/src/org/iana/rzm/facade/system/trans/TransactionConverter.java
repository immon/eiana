package org.iana.rzm.facade.system.trans;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.change.*;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;
import org.iana.rzm.facade.common.TrackDataVO;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Converts a domain transaction business and persistent object to a domain transaction
 * value object. Value objects are detached from a persistent store.
 *
 * @author Patrycja Wegrzynowicz
 */
class TransactionConverter {

    public static SimpleTransactionVO toSimpleTransactionVO(final Transaction trans) {
        if (trans == null) return null;

        SimpleTransactionVO ret = new SimpleTransactionVO();
        ret.setTransactionID(trans.getTransactionID());
        ret.setTicketID(trans.getTicketID());
        ret.setName(trans.getName());
        ret.setDomainName(trans.getCurrentDomain().getName());
        return ret;
    }

    public static TransactionVO toTransactionVO(final Transaction trans) {
        if (trans == null) return null;

        TransactionVO ret = new TransactionVO();
        ret.setTransactionID(trans.getTransactionID());
        ret.setTicketID(trans.getTicketID());
        ret.setName(trans.getName());
        ret.setDomainName(trans.getCurrentDomain().getName());

        List<TransactionActionVO> actions = new ArrayList<TransactionActionVO>();
        ObjectChange domainChange = trans.getDomainChange();
        if (domainChange != null) {
            Map<String, Change> fieldChanges = domainChange.getFieldChanges();
            if (fieldChanges.containsKey("whoisServer")) {
                SimpleChange simpleChange = (SimpleChange) fieldChanges.get("whoisServer");
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_WHOIS_SERVER);
                action.addChange(toChangeVOSimple("whoisServer", simpleChange));
                actions.add(action);
            }
            if (fieldChanges.containsKey("registryUrl")) {
                SimpleChange simpleChange = (SimpleChange) fieldChanges.get("registryUrl");
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_REGISTRATION_URL);
                action.addChange(toChangeVOSimple("registryUrl", simpleChange));
                actions.add(action);
            }

            if (fieldChanges.containsKey("adminContacts") || fieldChanges.containsKey("techContacts")) {
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_CONTACT);
                if (fieldChanges.containsKey("adminContacts")) {
                    CollectionChange adminChange = (CollectionChange) fieldChanges.get("adminContacts");
                    action.addChange(toChangeVO("adminContacts", adminChange));
                }
                if (fieldChanges.containsKey("techContacts")) {
                    CollectionChange techChange = (CollectionChange) fieldChanges.get("techContacts");
                    action.addChange(toChangeVO("techContacts", techChange));
                }
            }
        }
        ret.setDomainActions(actions);
        ret.setState(toStateVO(trans.getState()));
        ret.setStart(trans.getStart());
        ret.setEnd(trans.getEnd());

        return ret;
    }

    private static List<ChangeVO> toChangeVO(String fieldName, Change change) {
        return ChangeConverter.toChangeVO(fieldName, change);
    }

    static class ChangeConverter implements ChangeVisitor {

        private List<ChangeVO> ret = new ArrayList<ChangeVO>();
        private String field;

        private ChangeConverter(String field) {
            this.field = field;
        }

        static public List<ChangeVO> toChangeVO(String field, Change change) {
            ChangeConverter conv = new ChangeConverter(field);
            if (change != null) change.accept(conv);
            return conv.ret;
        }

        public void visitCollection(CollectionChange change) {
            ret.addAll(toChangeVOList(field, change));
        }

        public void visitObject(ObjectChange change) {
            ret.add(toChangeVOObject(field, change));
        }

        public void visitSimple(SimpleChange change) {
            ret.add(toChangeVOSimple(field, change));
        }
    }

    private static List<ChangeVO> toChangeVOList(String field, CollectionChange change) {
        List<ChangeVO> ret = new ArrayList<ChangeVO>();
        if (change.getAdded() != null) {
            for (Change add : change.getAdded()) {
                ret.addAll(toChangeVO(field, add));
            }
        }
        if (change.getRemoved() != null) {
            for (Change rem : change.getRemoved()) {
                ret.addAll(toChangeVO(field, rem));
            }
        }
        if (change.getModified() != null) {
            for (Change mod : change.getModified()) {
                ret.addAll(toChangeVO(field, mod));
            }
        }
        return ret;
    }

    private static ChangeVO toChangeVOSimple(String field, SimpleChange change) {
        ChangeVO ret = new ChangeVO();
        ret.setFieldName(field);
        ret.setType(ChangeVO.Type.valueOf(change.getType().toString()));
        ret.setValue(new StringValueVO(change.getOldValue(), change.getNewValue()));
        return ret;
    }

    private static ChangeVO toChangeVOObject(String field, ObjectChange change) {
        ChangeVO ret = new ChangeVO();
        ret.setFieldName(field);
        ret.setType(ChangeVO.Type.valueOf(change.getType().toString()));
        ObjectValueVO value = new ObjectValueVO(0, change.getId());
        Map<String, Change> changes = change.getFieldChanges();
        for (String fieldName : changes.keySet()) {
            Change fieldChange = changes.get(fieldName);
            value.addChanges(toChangeVO(fieldName, fieldChange));
        }
        ret.setValue(value);
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

    public static List<TransactionVO> toTransactionVOList(List<Transaction> transactionList) {
        List<TransactionVO> result = new ArrayList<TransactionVO>();
        for (Transaction transaction : transactionList) result.add(toTransactionVO(transaction));
        return result;
    }
}
