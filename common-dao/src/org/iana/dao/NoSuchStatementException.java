package org.iana.dao;

/**
 * <p>
 * This exception is thrown when attempting to access a statement (query or update) that does not exist.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class NoSuchStatementException extends DataAccessException {

    private String name;

    public NoSuchStatementException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
