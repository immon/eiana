package org.iana.rzm.mail.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class MailParserException extends Exception {
    public MailParserException() {
    }

    public MailParserException(String message) {
        super(message);
    }

    public MailParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailParserException(Throwable cause) {
        super(cause);
    }
}
