package org.iana.pgp;

/**
 * @author Jakub Laszkiewicz
 */
public class SignatureValidatorException extends Exception {
    public SignatureValidatorException() {
        super();
    }

    public SignatureValidatorException(String msg) {
        super(msg);
    }

    public SignatureValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureValidatorException(Throwable cause) {
        super(cause);
    }
}
