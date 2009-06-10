package org.iana.mail;

/**
 * @author Jakub Laszkiewicz
 */
public class MailSenderException extends Exception {
    public MailSenderException() {
    }

    public MailSenderException(String message) {
        super(message);
    }

    public MailSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailSenderException(Throwable cause) {
        super(cause);
    }
}
