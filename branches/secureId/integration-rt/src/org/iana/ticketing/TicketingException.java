package org.iana.ticketing;

/**
 * <p>
 * This exception is a general exception that represents any error occured while communicating with a ticketing system.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class TicketingException extends Exception {

    public TicketingException() {
    }

    public TicketingException(String message) {
        super(message);
    }

    public TicketingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketingException(Throwable cause) {
        super(cause);
    }
}
