package org.iana.rzm.facade.system.trans;

import org.iana.rzm.common.validators.CheckTool;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;

/**
 * Represents a set of transaction actions - a difference between modified domain and current domain.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TransactionActionsVO {

    private List<TransactionActionVO> actions = new ArrayList<TransactionActionVO>();
    private boolean nameServerAction = false;
    private boolean otherAction = false;

    public void addAction(TransactionActionVO action) {
        CheckTool.checkNull(action, "null transaction action");
        actions.add(action);
        if (!nameServerAction) nameServerAction = TransactionActionVO.MODIFY_NAME_SERVERS.equals(action.getName());
        if (!otherAction) otherAction = !TransactionActionVO.MODIFY_NAME_SERVERS.equals(action.getName());
    }

    public void setActions(Collection<TransactionActionVO> actions) {
        CheckTool.checkNull(actions, "null transaction actions");
        for (TransactionActionVO action : actions) {
            addAction(action);
        }
    }

    public boolean containsNameServerAction() {
        return nameServerAction;
    }

    public boolean containsOtherAction() {
        return otherAction;
    }

    public List<TransactionActionVO> getActions() {
        return Collections.unmodifiableList(actions);
    }
}
