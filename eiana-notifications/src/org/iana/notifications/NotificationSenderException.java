package org.iana.notifications;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NotificationSenderException extends Exception {

    public NotificationSenderException() {
    }

    public NotificationSenderException(String message) {
        super(message);
    }

    public NotificationSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationSenderException(Throwable cause) {
        super(cause);
    }
    
}
