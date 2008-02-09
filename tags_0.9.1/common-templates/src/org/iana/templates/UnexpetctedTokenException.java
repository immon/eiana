package org.iana.templates;

/**
 * @author Jakub Laszkiewicz
 */
public class UnexpetctedTokenException extends TemplatesServiceException {
    public UnexpetctedTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
