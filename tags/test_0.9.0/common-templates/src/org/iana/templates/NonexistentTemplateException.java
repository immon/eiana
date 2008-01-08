package org.iana.templates;

/**
 * @author Jakub Laszkiewicz
 */
public class NonexistentTemplateException extends TemplatesServiceException {
    public NonexistentTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
