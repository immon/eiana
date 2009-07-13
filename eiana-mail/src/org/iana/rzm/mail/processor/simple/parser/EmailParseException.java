package org.iana.rzm.mail.processor.simple.parser;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmailParseException extends Exception {

    public EmailParseException() {
    }

    public EmailParseException(String message) {
        super(message);
    }

    public EmailParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailParseException(Throwable cause) {
        super(cause);
    }

    public String getNotificationProducerName() {
        return "generalEmailExceptionNotificationProducer";
    }
}
