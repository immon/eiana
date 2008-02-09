/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications.exception;

public class InstancingNotificationException extends NotificationException {
    public InstancingNotificationException() {
        super();
    }

    public InstancingNotificationException(String message) {
        super(message);
    }

    public InstancingNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstancingNotificationException(Throwable cause) {
        super(cause);
    }
}
