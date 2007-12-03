package org.iana.rzm.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoSuchTransactionException extends TransactionException {

    private String id;

    public NoSuchTransactionException(long id) {
        this.id = ""+id;
    }

    public NoSuchTransactionException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
