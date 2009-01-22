package org.iana.rzm.facade.admin.trans;

/**
 * @author: Piotr Tkaczyk
 */
public class NoTransactionException extends Exception {
    private long id;

    public NoTransactionException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
