package org.iana.templates;

/**
 * @author Jakub Laszkiewicz
 */
public class UnexpectedElementException extends TemplatesServiceException {
    public UnexpectedElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
