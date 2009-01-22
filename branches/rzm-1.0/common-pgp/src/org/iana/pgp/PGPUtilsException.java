package org.iana.pgp;

/**
 * @author Jakub Laszkiewicz
 */
public class PGPUtilsException extends Exception {
    public PGPUtilsException() {
        super();
    }

    public PGPUtilsException(String msg) {
        super(msg);
    }

    public PGPUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PGPUtilsException(Throwable cause) {
        super(cause);
    }
}
