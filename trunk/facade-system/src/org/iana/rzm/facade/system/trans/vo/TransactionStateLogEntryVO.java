package org.iana.rzm.facade.system.trans.vo;

import java.io.Serializable;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionStateLogEntryVO implements Serializable {
    private String userName;
    private TransactionStateVO state;

    public TransactionStateLogEntryVO() {
    }

    public TransactionStateLogEntryVO(String userName, TransactionStateVO state) {
        this.userName = userName;
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public TransactionStateVO getState() {
        return state;
    }

    public void setState(TransactionStateVO state) {
        this.state = state;
    }
}
