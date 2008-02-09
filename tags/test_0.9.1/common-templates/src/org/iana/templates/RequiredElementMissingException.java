package org.iana.templates;

/**
 * @author Jakub Laszkiewicz
 */
public class RequiredElementMissingException extends TemplatesServiceException {
    public RequiredElementMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
