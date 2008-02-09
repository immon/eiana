package org.iana.templates;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplatesServiceException extends Exception {
    public TemplatesServiceException() {
    }

    public TemplatesServiceException(String message) {
        super(message);
    }

    public TemplatesServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplatesServiceException(Throwable cause) {
        super(cause);
    }
}
