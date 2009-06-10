package org.iana.templates.def.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplateDefParseException extends Exception {
    public TemplateDefParseException() {
        super();
    }

    public TemplateDefParseException(String msg) {
        super(msg);
    }

    public TemplateDefParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateDefParseException(Throwable cause) {
        super(cause);
    }
}
