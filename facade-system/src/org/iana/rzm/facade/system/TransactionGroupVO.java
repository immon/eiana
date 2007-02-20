package org.iana.rzm.facade.system;

import org.iana.rzm.facade.user.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionGroupVO {

    private List<TransactionActionVO> actions;
    private int estimatedProcessingTime;
    private Set<RoleVO.Type> requiredConfirmations;

    public List<TransactionActionVO> getActions() {
        return actions;
    }

    public void setActions(List<TransactionActionVO> actions) {
        this.actions = actions;
    }

    public int getEstimatedProcessingTime() {
        return estimatedProcessingTime;
    }

    public void setEstimatedProcessingTime(int estimatedProcessingTime) {
        this.estimatedProcessingTime = estimatedProcessingTime;
    }

    public Set<RoleVO.Type> getRequiredConfirmations() {
        return requiredConfirmations;
    }

    public void setRequiredConfirmations(Set<RoleVO.Type> requiredConfirmations) {
        this.requiredConfirmations = requiredConfirmations;
    }
}
