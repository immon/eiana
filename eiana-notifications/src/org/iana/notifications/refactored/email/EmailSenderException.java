package org.iana.notifications.refactored.email;

import org.iana.notifications.refactored.NotificationSenderException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmailSenderException extends NotificationSenderException {

    public EmailSenderException() {
    }

    public EmailSenderException(String message) {
        super(message);
    }

    public EmailSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSenderException(Throwable cause) {
        super(cause);
    }
}
