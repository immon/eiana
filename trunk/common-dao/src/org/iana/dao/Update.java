package org.iana.dao;

/**
 * <p>
 * This interface represent a general SQL named update. A concrete <code>DataAccessObject</code> realization
 * has a configured set of provided updates.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface Update<T> extends Statement {

    void execute() throws DataAccessException;

    Update<T> clone();
}
