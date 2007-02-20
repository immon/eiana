package org.iana.dao;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DataAccessFailure extends DataAccessException {

    public DataAccessFailure() {
    }

    public DataAccessFailure(String message) {
        super(message);
    }

    public DataAccessFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessFailure(Throwable cause) {
        super(cause);
    }
}
