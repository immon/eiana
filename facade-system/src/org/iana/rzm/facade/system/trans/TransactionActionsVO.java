package org.iana.rzm.facade.system.trans;

import org.iana.rzm.common.validators.CheckTool;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;

/**
 * Represents a set of transaction groups - a difference between modified domain and current domain.
 * The transaction groups are grouped to show whether they may be combined or not into a single transaction request.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TransactionActionsVO {

    private List<TransactionActionGroupVO> groups = new ArrayList<TransactionActionGroupVO>();
    private int nameServerActions = 0;
    private boolean otherAction = false;

    public void addGroup(TransactionActionGroupVO group) {
        CheckTool.checkNull(group, "null transaction group");
        groups.add(group);
        nameServerActions += group.getNameServerActions();
        if (!otherAction) otherAction = group.containsOtherAction();
    }

    public void setGroups(Collection<TransactionActionGroupVO> groups) {
        for (TransactionActionGroupVO group : groups) {
            addGroup(group);
        }
    }

    public void setOtherAction(boolean otherAction) {
        this.otherAction = otherAction;
    }

    public int getNameServerActions() {
        return nameServerActions;
    }

    public boolean containsNameServerAction() {
        return nameServerActions > 0;
    }

    public boolean containsOtherAction() {
        return otherAction;
    }

    public List<TransactionActionVO> getActions() {
        List<TransactionActionVO> ret = new ArrayList<TransactionActionVO>();
        for (TransactionActionGroupVO group : groups) {
            ret.addAll(group.getActions());
        }
        return ret;
    }

    public List<TransactionActionGroupVO> getGroups() {
        return Collections.unmodifiableList(groups);
    }
}
