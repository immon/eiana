package org.iana.rzm.trans.epp;

/**
 * @author Jakub Laszkiewicz
 */
public class EppChangeRequestPollRspVisitorException extends Exception {
    public EppChangeRequestPollRspVisitorException() {
    }

    public EppChangeRequestPollRspVisitorException(String message) {
        super(message);
    }

    public EppChangeRequestPollRspVisitorException(String message, Throwable cause) {
        super(message, cause);
    }

    public EppChangeRequestPollRspVisitorException(Throwable cause) {
        super(cause);
    }
}
