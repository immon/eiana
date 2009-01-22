package org.iana.rzm.trans;

/**
 * @author Jakub Laszkiewicz
 */
public class IllegalTransactionStateException extends TransactionException {
    TransactionState state;

    public IllegalTransactionStateException(TransactionState state) {
        super("Illegal transaction state: " + state);
        this.state = state;
    }

    public TransactionState getState() {
        return state;
    }
}
