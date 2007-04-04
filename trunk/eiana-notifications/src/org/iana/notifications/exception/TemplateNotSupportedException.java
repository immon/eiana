package org.iana.notifications.exception;

/**
 * @author Piotr Tkaczyk
 */

public class TemplateNotSupportedException extends NotificationException {

    public TemplateNotSupportedException() {
       super(); 
    }

    public TemplateNotSupportedException(String message) {
        super(message);
    }
}
