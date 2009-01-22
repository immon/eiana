package org.iana.notifications.template.simple;

/**
 * @author Patrycja Wegrzynowicz
 */
public class StringTemplateException extends Exception {

    public StringTemplateException() {
    }

    public StringTemplateException(String message) {
        super(message);
    }

    public StringTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringTemplateException(Throwable cause) {
        super(cause);
    }
}
