package org.iana.rzm.facade.system.trans.vo.changes;

import org.iana.rzm.common.validators.*;

import java.io.*;
import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionActionGroupVO implements Serializable {

    private boolean splittable = false;
    private List<TransactionActionVO> actions = new ArrayList<TransactionActionVO>();
    private int nameServerActions;
    private boolean otherAction;
    private boolean glueChange = false;

    public TransactionActionGroupVO(boolean splittable) {
        this.splittable = splittable;
    }

    public TransactionActionGroupVO(TransactionActionVO action, boolean glueChange) {
        addAction(action);
        this.glueChange = glueChange;
    }

    public TransactionActionGroupVO(TransactionActionVO action1, TransactionActionVO action2) {
        addAction(action1);
        addAction(action2);
    }

    public TransactionActionGroupVO(boolean splittable, List<TransactionActionVO> actions) {
        CheckTool.checkNull(actions, "null actions");
        this.splittable = splittable;
        for (TransactionActionVO action : actions) {
            addAction(action);
        }
    }

    public void addAction(TransactionActionVO action) {
        actions.add(action);
        boolean nameServerAction = TransactionActionVO.MODIFY_NAME_SERVERS.equals(action.getName());
        if (nameServerAction) nameServerActions++;
        if (!otherAction) otherAction = !nameServerAction;
    }

    public int getNameServerActions() {
        return nameServerActions;
    }

    public boolean containsOtherAction() {
        return otherAction;
    }

    public boolean containsNameServerAction() {
        return nameServerActions > 0;
    }


    public boolean isGlueChange() {
        return glueChange;
    }

    /**
     * Determines whether or not this group can be splitted into
     * the separate transactions.
     *
     * @return true if this group can be splitted into the separate transactions, false otherwise.
     */
    public boolean isSplittable() {
        return splittable;
    }

    /**
     * Returns a list of transaction actions contained in this group.
     *
     * @return the list of transaction actions contained in this group.
     */
    public List<TransactionActionVO> getActions() {
        return actions;
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }
}
