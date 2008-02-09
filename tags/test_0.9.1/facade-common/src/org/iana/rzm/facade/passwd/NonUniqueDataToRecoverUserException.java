package org.iana.rzm.facade.passwd;

/**
 * Exception thrown when data provided to recover a user name does not identify a unique user (possible mismatch - two or more
 * users having the same email and password).
 *
 * @author Patrycja Wegrzynowicz
 */
public class NonUniqueDataToRecoverUserException extends Exception {

    public NonUniqueDataToRecoverUserException() {
    }

    public NonUniqueDataToRecoverUserException(String message) {
        super(message);
    }

    public NonUniqueDataToRecoverUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonUniqueDataToRecoverUserException(Throwable cause) {
        super(cause);
    }
}
