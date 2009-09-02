package org.iana.rzm.mail.processor.usdoc;

import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.simple.data.Message;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCAuthenticationFailureException extends USDoCProcessException {

    public USDoCAuthenticationFailureException(Message mailMsg, TransactionVO transaction) {
        super(mailMsg, transaction);
    }

    public USDoCAuthenticationFailureException(String string, Message mailMsg, TransactionVO transaction) {
        super(string, mailMsg, transaction);
    }

    public USDoCAuthenticationFailureException(String string, Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(string, throwable, mailMsg, transaction);
    }

    public USDoCAuthenticationFailureException(Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(throwable, mailMsg, transaction);
    }
}
