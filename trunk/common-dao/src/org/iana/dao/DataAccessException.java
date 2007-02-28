package org.iana.dao;

/**
 * <p>
 * This exception is a general exception thrown by this data access package.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class DataAccessException extends Exception {

    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
