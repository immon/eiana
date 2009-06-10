package org.iana.rzm.facade.system.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionCannotBeWithdrawnException extends TransactionServiceException {

    String transactionState;

    public TransactionCannotBeWithdrawnException(long transactionId, String transactionState) {
        super(transactionId);
        this.transactionState = transactionState;
    }

}
