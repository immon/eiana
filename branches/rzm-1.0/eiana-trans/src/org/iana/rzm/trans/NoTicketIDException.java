package org.iana.rzm.trans;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoTicketIDException extends TransactionException {

    public NoTicketIDException() {
    }

    public NoTicketIDException(String message) {
        super(message);
    }

    public NoTicketIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTicketIDException(Throwable cause) {
        super(cause);
    }
}
