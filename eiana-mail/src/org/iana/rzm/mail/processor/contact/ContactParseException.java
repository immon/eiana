package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.mail.processor.simple.parser.EmailParseException;

/**
 * @author Piotr Tkaczyk
 */
public class ContactParseException extends EmailParseException {

    public ContactParseException() {
    }

    public ContactParseException(String message) {
        super(message);
    }

    public ContactParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContactParseException(Throwable cause) {
        super(cause);
    }

    public String getNotificationProducerName() {
        return "contactParserExceptionNotificationProducer";
    }
}
