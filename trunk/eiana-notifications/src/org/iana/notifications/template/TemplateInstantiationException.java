package org.iana.notifications.template;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TemplateInstantiationException extends Exception {

    public TemplateInstantiationException() {
    }

    public TemplateInstantiationException(String message) {
        super(message);
    }

    public TemplateInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateInstantiationException(Throwable cause) {
        super(cause);
    }
    
}
