package org.iana.rzm.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionException extends Exception {
    public TransactionException() {
        super();
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
