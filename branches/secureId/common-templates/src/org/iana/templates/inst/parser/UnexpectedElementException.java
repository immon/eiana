package org.iana.templates.inst.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class UnexpectedElementException extends TemplateParseException {
    public UnexpectedElementException(String elementName) {
        super("Unexpected element encountered: [" + elementName + "]");
    }
}
