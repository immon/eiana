package org.iana.notifications.template;

/**
 * @author Piotr Tkaczyk
 */
public class TemplateNotFoundException extends Exception {

    public TemplateNotFoundException() {
    }

    public TemplateNotFoundException(String string) {
        super(string);
    }

    public TemplateNotFoundException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public TemplateNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
