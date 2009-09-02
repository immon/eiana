package org.iana.rzm.mail.processor.usdoc;

import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCProcessException extends EmailProcessException {

    private TransactionVO transaction;

    public USDoCProcessException(Message mailMsg, TransactionVO transaction) {
        super(mailMsg);
        this.transaction = transaction;
    }

    public USDoCProcessException(String string, Message mailMsg, TransactionVO transaction) {
        super(string, mailMsg);
        this.transaction = transaction;
    }

    public USDoCProcessException(String string, Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(string, throwable, mailMsg);
        this.transaction = transaction;
    }

    public USDoCProcessException(Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(throwable, mailMsg);
        this.transaction = transaction;
    }

    public TransactionVO getTransaction() {
        return transaction;
    }
}
