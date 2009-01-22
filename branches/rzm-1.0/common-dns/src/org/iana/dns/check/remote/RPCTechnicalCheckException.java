package org.iana.dns.check.remote;

/**
 * @author Jakub Laszkiewicz
 */
public class RPCTechnicalCheckException extends Exception {
    public RPCTechnicalCheckException() {
    }

    public RPCTechnicalCheckException(String message) {
        super(message);
    }

    public RPCTechnicalCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCTechnicalCheckException(Throwable cause) {
        super(cause);
    }
}
