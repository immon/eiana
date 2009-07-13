package org.iana.rzm.mail.processor.simple.processor;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmailProcessException extends Exception {

    public EmailProcessException() {
    }

    public EmailProcessException(String message) {
        super(message);
    }

    public EmailProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailProcessException(Throwable cause) {
        super(cause);
    }

    public String getNotificationProducerName() {
        return "generalEmailExceptionNotificationProducer";
    }
}
