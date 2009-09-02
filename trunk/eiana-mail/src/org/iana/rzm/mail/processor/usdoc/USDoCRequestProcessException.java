package org.iana.rzm.mail.processor.usdoc;

import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.simple.data.Message;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCRequestProcessException extends USDoCProcessException {


    public USDoCRequestProcessException(Message mailMsg, TransactionVO transaction) {
        super(mailMsg, transaction);
    }

    public USDoCRequestProcessException(String string, Message mailMsg, TransactionVO transaction) {
        super(string, mailMsg, transaction);
    }

    public USDoCRequestProcessException(String string, Message mailMsg) {
        super(string, mailMsg, null);
    }

    public USDoCRequestProcessException(String string, Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(string, throwable, mailMsg, transaction);
    }

    public USDoCRequestProcessException(String string, Throwable throwable, Message mailMsg) {
        super(string, throwable, mailMsg, null);
    }

    public USDoCRequestProcessException(Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(throwable, mailMsg, transaction);
    }
}
