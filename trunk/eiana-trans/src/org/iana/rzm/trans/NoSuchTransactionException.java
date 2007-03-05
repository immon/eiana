package org.iana.rzm.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoSuchTransactionException extends TransactionException {

    private long id;

    public NoSuchTransactionException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
