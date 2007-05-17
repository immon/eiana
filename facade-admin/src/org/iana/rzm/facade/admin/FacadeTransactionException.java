package org.iana.rzm.facade.admin;

/**
 * @author: Piotr Tkaczyk
 */
public class FacadeTransactionException extends Exception {

    public FacadeTransactionException() {
    }

    public FacadeTransactionException(String string) {
        super(string);
    }

    public FacadeTransactionException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public FacadeTransactionException(Throwable throwable) {
        super(throwable);
    }
}
