package org.iana.rzm.mail.processor.simple.processor;

import org.iana.rzm.mail.processor.simple.data.Message;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmailProcessException extends Exception {

    private Message mailMsg;

    public EmailProcessException() {}

    public EmailProcessException(Message mailMsg) {
        this.mailMsg = mailMsg;
    }

    public EmailProcessException(String string, Message mailMsg) {
        super(string);
        this.mailMsg = mailMsg;
    }

    public EmailProcessException(String string, Throwable throwable, Message mailMsg) {
        super(string, throwable);
        this.mailMsg = mailMsg;
    }

    public EmailProcessException(Throwable throwable, Message mailMsg) {
        super(throwable);
        this.mailMsg = mailMsg;
    }

    public Message getMailMsg() {
        return mailMsg;
    }
}
