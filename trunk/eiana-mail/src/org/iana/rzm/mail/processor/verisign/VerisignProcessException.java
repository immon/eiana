package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;

/**
 * @author Piotr Tkaczyk
 */
public class VerisignProcessException extends EmailProcessException {

    public VerisignProcessException(Message mailMsg) {
        super(mailMsg);
    }

    public VerisignProcessException(String string, Message mailMsg) {
        super(string, mailMsg);
    }

    public VerisignProcessException(String string, Throwable throwable, Message mailMsg) {
        super(string, throwable, mailMsg);
    }

    public VerisignProcessException(Throwable throwable, Message mailMsg) {
        super(throwable, mailMsg);
    }
}
