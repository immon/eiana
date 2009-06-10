package org.iana.notifications.producers;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NotificationProducerException extends Exception {

    public NotificationProducerException() {
    }

    public NotificationProducerException(String message) {
        super(message);
    }

    public NotificationProducerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationProducerException(Throwable cause) {
        super(cause);
    }
}
