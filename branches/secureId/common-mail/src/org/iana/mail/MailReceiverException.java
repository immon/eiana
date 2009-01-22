package org.iana.mail;

/**
 * @author Jakub Laszkiewicz
 */
public class MailReceiverException extends Exception {
    public MailReceiverException() {
    }

    public MailReceiverException(String message) {
        super(message);
    }

    public MailReceiverException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailReceiverException(Throwable cause) {
        super(cause);
    }
}
