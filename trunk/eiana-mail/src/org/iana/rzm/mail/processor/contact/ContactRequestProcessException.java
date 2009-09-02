package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;

/**
 * @author Piotr Tkaczyk
 */
public class ContactRequestProcessException extends EmailProcessException {

    private TransactionVO transaction;

    public ContactRequestProcessException(Message mailMsg, TransactionVO transaction) {
        super(mailMsg);
        this.transaction = transaction;
    }

    public ContactRequestProcessException(String string, Message mailMsg, TransactionVO transaction) {
        super(string, mailMsg);
        this.transaction = transaction;
    }

    public ContactRequestProcessException(String string, Message mailMsg) {
        this(string, mailMsg, null);
    }

    public ContactRequestProcessException(String string, Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(string, throwable, mailMsg);
        this.transaction = transaction;
    }

    public ContactRequestProcessException(Throwable throwable, Message mailMsg, TransactionVO transaction) {
        super(throwable, mailMsg);
        this.transaction = transaction;
    }

    public TransactionVO getTransaction() {
        return transaction;
    }
}
