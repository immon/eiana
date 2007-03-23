package org.iana.rzm.facade.system.trans;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.change.ObjectChange;
import org.iana.rzm.trans.change.Change;
import org.iana.rzm.trans.change.SimpleChange;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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
        ObjectChange domainChange = trans.getDomainChange();
        Map<String, Change> fieldChanges = domainChange.getFieldChanges();
        if (fieldChanges.containsKey("whoisServer")) {
            SimpleChange simpleChange = (SimpleChange) fieldChanges.get("whoisServer");
            TransactionActionVO action = new TransactionActionVO();
            action.setName(TransactionActionVO.Name.MODIFY_WHOIS_SERVER);
            action.addChange(toSimpleChangeVO("whoisServer", simpleChange));
            actions.add(action);
        }
        if (fieldChanges.containsKey("registryUrl")) {
            SimpleChange simpleChange = (SimpleChange) fieldChanges.get("registryUrl");
            TransactionActionVO action = new TransactionActionVO();
            action.setName(TransactionActionVO.Name.MODIFY_REGISTRATION_URL);
            action.addChange(toSimpleChangeVO("registryUrl", simpleChange));
            actions.add(action);
        }
        // todo: modify contact
        ret.setDomainActions(actions);

        ret.setState(toStateVO(trans.getState()));
        ret.setStart(trans.getStart());
        ret.setEnd(trans.getEnd());

        return ret;
    }

    private static ChangeVO toSimpleChangeVO(String field, SimpleChange change) {
        ChangeVO ret = new ChangeVO();
        ret.setFieldName(field);
        ret.setType(ChangeVO.Type.valueOf(change.getType().toString()));
        ret.setValue(new StringValueVO(change.getOldValue(), change.getNewValue()));
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
