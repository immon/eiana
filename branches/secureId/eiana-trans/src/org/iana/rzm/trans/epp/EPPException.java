package org.iana.rzm.trans.epp;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPException extends Exception {
    public EPPException() {
    }

    public EPPException(int code, String message, List<String> errors) {
        super("Error code: " + code + " - " + message + ", " + errors);
    }

    public EPPException(String message) {
        super(message);
    }

    public EPPException(String message, Throwable cause) {
        super(message, cause);
    }

    public EPPException(Throwable cause) {
        super(cause);
    }
}
