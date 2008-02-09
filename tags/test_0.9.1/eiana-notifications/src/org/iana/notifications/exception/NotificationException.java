package org.iana.notifications.exception;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NotificationException extends Exception {
    public NotificationException() {
        super();
    }

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationException(Throwable cause) {
        super(cause);
    }
}
