package org.iana.rzm.mail.processor.usdoc;

import org.iana.rzm.mail.processor.simple.parser.EmailParseException;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCParseException extends EmailParseException {

    public USDoCParseException() {
    }

    public USDoCParseException(String message) {
        super(message);
    }

    public USDoCParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public USDoCParseException(Throwable cause) {
        super(cause);
    }

    public String getNotificationProducerName() {
        return "usdocParseExceptionNotificationProducer";
    }
}
