package org.iana.rzm.trans.confirmation;

/**
 * @author Jakub Laszkiewicz
 */
public class AlreadyAcceptedByUser extends Exception {
    public AlreadyAcceptedByUser() {
        super();
    }

    public AlreadyAcceptedByUser(String message) {
        super(message);
    }

    public AlreadyAcceptedByUser(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyAcceptedByUser(Throwable cause) {
        super(cause);
    }
}
