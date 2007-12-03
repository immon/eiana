package org.iana.rzm.trans;

import org.iana.rzm.trans.epp.EppChangeRequestPollRspVisitorException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionException extends EppChangeRequestPollRspVisitorException {
    public TransactionException() {
        super();
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
