package org.iana.templates.validator;

/**
 * @author Jakub Laszkiewicz
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
