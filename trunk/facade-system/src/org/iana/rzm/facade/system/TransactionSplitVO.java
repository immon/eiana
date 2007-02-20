package org.iana.rzm.facade.system;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionSplitVO {

    private List<TransactionGroupVO> groups;

    public List<TransactionGroupVO> getGroups() {
        return groups;
    }

    public void setGroups(List<TransactionGroupVO> groups) {
        this.groups = groups;
    }
}
