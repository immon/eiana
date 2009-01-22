package org.iana.rzm.facade.system.trans;

/**
 * A base class of exceptions related to transaction management including transaction creation and processing.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class TransactionServiceException extends Exception {

    long transactionId;

    public TransactionServiceException() {
    }

    public TransactionServiceException(long transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionServiceException(String message) {
        super(message);
    }

    public TransactionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionServiceException(Throwable cause) {
        super(cause);
    }


    public long getTransactionId() {
        return transactionId;
    }
}
