package org.iana.templates.validator;

/**
 * @author Jakub Laszkiewicz
 */
public class RequiredElementMissingException extends ValidationException {
    public RequiredElementMissingException(String elementName) {
        super("Required element missing: [" + elementName + "]");
    }
}
