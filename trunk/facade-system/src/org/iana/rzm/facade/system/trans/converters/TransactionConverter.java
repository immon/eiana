package org.iana.rzm.facade.system.trans.converters;

import org.iana.objectdiff.*;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.facade.system.trans.vo.*;
import org.iana.rzm.facade.system.trans.vo.changes.ChangeVO;
import org.iana.rzm.facade.system.trans.vo.changes.ObjectValueVO;
import org.iana.rzm.facade.system.trans.vo.changes.StringValueVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionVO;
import org.iana.rzm.facade.user.converter.RoleConverter;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.TransactionStateLogEntry;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;

import java.util.*;

/**
 * Converts a domain transaction business and persistent object to a domain transaction
 * value object. Value objects are detached from a persistent store.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TransactionConverter {

    private static Map<String, List<String>> order = new HashMap<String, List<String>>();

    static {
        order.put("nameServers", Arrays.asList("name", "addresses"));
    }

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
        if (trans.getCurrentDomain() != null) ret.setDomainName(trans.getCurrentDomain().getName());

        ret.setDomainActions(toTransactionActionVO(trans.getDomainChange()));

        ret.setState(toStateVO(trans.getState()));
        ret.setStateLog(toTransactionStateLogEntryVOList(trans.getStateLog()));
        ret.setStart(trans.getStart());
        ret.setEnd(trans.getEnd());
        ret.setRedelegation(trans.isRedelegation());
        ret.setSubmitterEmail(trans.getSubmitterEmail());

        Set<ContactIdentity> received = trans.getIdentitiesThatAccepted();
        for (ContactIdentity cid : received)
            ret.addConfirmation(new ConfirmationVO(RoleConverter.systemRolesMap.get(cid.getType()), true, cid.getName(), cid.isNewContact()));
        Set<ContactIdentity> outstanding = trans.getIdentitiesSupposedToAccept();
        for (ContactIdentity cid : outstanding)
            ret.addConfirmation(new ConfirmationVO(RoleConverter.systemRolesMap.get(cid.getType()), false, cid.getName(), cid.isNewContact()));

        ret.setCreated(trans.getCreated());
        ret.setCreatedBy(trans.getCreatedBy());
        ret.setModified(trans.getModified());
        ret.setModifiedBy(trans.getModifiedBy());

        ret.setTokens(getTokens(trans));

        ret.setComment(trans.getComment());
        ret.setStateMessage(trans.getStateMessage());

        return ret;
    }

    public static List<TransactionActionVO> toTransactionActionVO(ObjectChange domainChange) {
        List<TransactionActionVO> actions = new ArrayList<TransactionActionVO>();
        if (domainChange != null) {
            Map<String, Change> fieldChanges = domainChange.getFieldChanges();
            if (fieldChanges.containsKey("specialInstructions") ||
                    fieldChanges.containsKey("type") ||
                    fieldChanges.containsKey("description") ||
                    fieldChanges.containsKey("enableEmails")) {
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_OTHER_ATTRIBUTES);
                for (String field : new String[]{"specialInstructions", "type", "description", "enableEmails"}) {
                    SimpleChange simpleChange = (SimpleChange) fieldChanges.get(field);
                    if (simpleChange != null) action.addChange(toChangeVOSimple(field, simpleChange));
                }
                actions.add(action);
            }
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
            if (fieldChanges.containsKey("adminContact")) {
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_AC);
                ObjectChange adminChange = (ObjectChange) fieldChanges.get("adminContact");
                action.addChange(toChangeVO("adminContact", adminChange));
                actions.add(action);
            }
            if (fieldChanges.containsKey("techContact")) {
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_TC);
                ObjectChange adminChange = (ObjectChange) fieldChanges.get("techContact");
                action.addChange(toChangeVO("techContact", adminChange));
                actions.add(action);
            }
            if (fieldChanges.containsKey("supportingOrg")) {
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_SO);
                ObjectChange supportingOrgChange = (ObjectChange) fieldChanges.get("supportingOrg");
                action.addChange(toChangeVO("supportingOrg", supportingOrgChange));
                actions.add(action);
            }
            if (fieldChanges.containsKey("nameServers")) {
                TransactionActionVO action = new TransactionActionVO();
                action.setName(TransactionActionVO.MODIFY_NAME_SERVERS);
                CollectionChange nameServersChange = (CollectionChange) fieldChanges.get("nameServers");
                action.addChange(toChangeVO("nameServers", nameServersChange));
                actions.add(action);
            }
        }
        return actions;
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
        List<String> fieldNames = order.containsKey(field) ? order.get(field) : new ArrayList<String>(changes.keySet());
        for (String fieldName : fieldNames) {
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

    public static TransactionStateLogEntryVO toTransactionStateLogEntryVO(TransactionStateLogEntry stateLogEntry) {
        TransactionStateLogEntryVO result = new TransactionStateLogEntryVO();
        result.setState(toStateVO(stateLogEntry.getState()));
        result.setUserName(stateLogEntry.getUserName());
        return result;
    }

    public static List<TransactionStateLogEntryVO> toTransactionStateLogEntryVOList(List<TransactionStateLogEntry> stateLog) {
        List<TransactionStateLogEntryVO> stateLogVO = new ArrayList<TransactionStateLogEntryVO>();
        for (TransactionStateLogEntry entry : stateLog)
            stateLogVO.add(toTransactionStateLogEntryVO(entry));
        return stateLogVO;
    }

    private static List<String> getTokens(Transaction transaction) {
        List<String> result = new ArrayList<String>();
        ContactConfirmations cc = transaction.getTransactionData().getContactConfirmations();
        if (cc != null)
            for (Identity identity : cc.getUsersAbleToAccept())
                if (identity instanceof ContactIdentity) {
                    ContactIdentity contactIdentity = (ContactIdentity) identity;
                    result.add(contactIdentity.getToken());
                }
        return result;
    }
}
