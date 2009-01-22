package org.iana.templates.validator;

/**
 * @author Jakub Laszkiewicz
 */
public class UnexpectedElementException extends ValidationException {
    public UnexpectedElementException(String elementName) {
        super("Unexpected element encountered: [" + elementName + "]");
    }
}
