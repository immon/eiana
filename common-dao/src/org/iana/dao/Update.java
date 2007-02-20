package org.iana.dao;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Update<T> extends Statement {

    void execute() throws DataAccessException;
}
