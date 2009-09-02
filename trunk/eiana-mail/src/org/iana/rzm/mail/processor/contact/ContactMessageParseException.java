package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.mail.processor.simple.parser.EmailParseException;

/**
 * @author Piotr Tkaczyk
 */
public class ContactMessageParseException extends EmailParseException {

    private Long ticketId;

    public ContactMessageParseException() {}

    public ContactMessageParseException(String message) {
        super(message);
    }

    public ContactMessageParseException(String message, Throwable cause, Long ticketId) {
        super(message, cause);
        this.ticketId = ticketId;
    }

    public ContactMessageParseException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public ContactMessageParseException(Throwable cause) {
        super(cause);
    }

    public Long getTicketId() {
        return ticketId;
    }
}
