package org.iana.notifications.refactored.template.def;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TemplateInitializationException extends Exception {

    public TemplateInitializationException() {
    }

    public TemplateInitializationException(String message) {
        super(message);
    }

    public TemplateInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateInitializationException(Throwable cause) {
        super(cause);
    }
}
