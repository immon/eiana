package org.iana.dao;

/**
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
