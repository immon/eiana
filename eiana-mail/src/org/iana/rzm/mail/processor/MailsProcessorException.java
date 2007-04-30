package org.iana.rzm.mail.processor;

/**
 * @author Jakub Laszkiewicz
 */
public class MailsProcessorException extends Exception {
    public MailsProcessorException() {
    }

    public MailsProcessorException(String message) {
        super(message);
    }

    public MailsProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailsProcessorException(Throwable cause) {
        super(cause);
    }
}
