package org.iana.rzm.trans.confirmation;

/**
 * @author Jakub Laszkiewicz
 */
public class NotAcceptableByUser extends Exception {
    public NotAcceptableByUser() {
        super();
    }

    public NotAcceptableByUser(String message) {
        super(message);
    }

    public NotAcceptableByUser(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAcceptableByUser(Throwable cause) {
        super(cause);
    }
}
