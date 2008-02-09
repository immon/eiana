package org.iana.templates.inst.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplateParseException extends Exception {
    public TemplateParseException() {
        super();
    }

    public TemplateParseException(String msg) {
        super(msg);
    }

    public TemplateParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateParseException(Throwable cause) {
        super(cause);
    }
}
