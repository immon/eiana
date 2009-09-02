package org.iana.rzm.mail.processor.usdoc;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCMessageParseException extends USDoCParseException {

    Long ticketId;

    public USDoCMessageParseException(Long ticketId) {
        this.ticketId = ticketId;
    }

    public USDoCMessageParseException(String message, Long ticketId) {
        super(message);
        this.ticketId = ticketId;
    }

    public USDoCMessageParseException(String message, Throwable cause, Long ticketId) {
        super(message, cause);
        this.ticketId = ticketId;
    }

    public USDoCMessageParseException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public USDoCMessageParseException(Throwable cause, Long ticketId) {
        super(cause);
        this.ticketId = ticketId;
    }

    public USDoCMessageParseException(Throwable cause) {
        this(cause,  null);
    }

    public Long getTicketId() {
        return ticketId;
    }
}
