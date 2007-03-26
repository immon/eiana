package org.iana.rzm.trans;

/**
 * @author Jakub Laszkiewicz
 */
public class UserAlreadyAccepted extends TransactionException {
    public UserAlreadyAccepted() {
        super();
    }

    public UserAlreadyAccepted(String message) {
        super(message);
    }

    public UserAlreadyAccepted(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyAccepted(Throwable cause) {
        super(cause);
    }
}
