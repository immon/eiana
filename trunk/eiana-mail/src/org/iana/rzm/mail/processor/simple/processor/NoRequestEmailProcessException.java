package org.iana.rzm.mail.processor.simple.processor;

/**
 * @author Piotr Tkaczyk
 */
public class NoRequestEmailProcessException extends EmailProcessException {

    public String getNotificationProducerName() {
        return "requestNotFoundNotificationProducer";
    }

    public NoRequestEmailProcessException() {
    }

    public NoRequestEmailProcessException(String message) {
        super(message);
    }

    public NoRequestEmailProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRequestEmailProcessException(Throwable cause) {
        super(cause);
    }
}
