package org.iana.rzm.mail.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class MailParserTemplateException extends MailParserException {
    public MailParserTemplateException() {
    }

    public MailParserTemplateException(String message) {
        super(message);
    }

    public MailParserTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailParserTemplateException(Throwable cause) {
        super(cause);
    }
}
