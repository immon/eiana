package org.iana.rzm.trans;

/**
 * @author Jakub Laszkiewicz
 */
public class UserConfirmationNotExpected extends TransactionException {
    public UserConfirmationNotExpected() {
        super();
    }

    public UserConfirmationNotExpected(String message) {
        super(message);
    }

    public UserConfirmationNotExpected(String message, Throwable cause) {
        super(message, cause);
    }

    public UserConfirmationNotExpected(Throwable cause) {
        super(cause);
    }
}
