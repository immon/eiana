/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications.exception;

public class InitializationNotificationException extends NotificationException {
    public InitializationNotificationException() {
        super();
    }

    public InitializationNotificationException(String message) {
        super(message);
    }

    public InitializationNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationNotificationException(Throwable cause) {
        super(cause);
    }
}
