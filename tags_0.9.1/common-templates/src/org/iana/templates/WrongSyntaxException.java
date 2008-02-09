package org.iana.templates;

/**
 * @author Jakub Laszkiewicz
 */
public class WrongSyntaxException extends TemplatesServiceException {
    public WrongSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
