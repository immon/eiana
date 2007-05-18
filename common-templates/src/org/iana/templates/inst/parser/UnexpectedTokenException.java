package org.iana.templates.inst.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class UnexpectedTokenException extends TemplateParseException {
    public UnexpectedTokenException(String token) {
        super("Unexpected token encountered: [" + token + "]");
    }
}
