package org.iana.rzm.facade.admin.trans;

import org.iana.rzm.facade.system.trans.TransactionServiceException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class IllegalTransactionStateException extends TransactionServiceException {

    String state;

    public IllegalTransactionStateException(long transactionId, String currentState) {
        super(transactionId);
        this.state = currentState;
    }

    public String getState() {
        return state;
    }
}
