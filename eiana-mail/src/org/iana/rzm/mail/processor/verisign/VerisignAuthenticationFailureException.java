package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;

/**
 * @author Piotr Tkaczyk
 */
public class VerisignAuthenticationFailureException extends EmailProcessException {

    public VerisignAuthenticationFailureException(Message mailMsg) {
        super(mailMsg);
    }

    public VerisignAuthenticationFailureException(String string, Message mailMsg) {
        super(string, mailMsg);
    }

    public VerisignAuthenticationFailureException(String string, Throwable throwable, Message mailMsg) {
        super(string, throwable, mailMsg);
    }

    public VerisignAuthenticationFailureException(Throwable throwable, Message mailMsg) {
        super(throwable, mailMsg);
    }
}
