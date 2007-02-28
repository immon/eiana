package org.iana.dao;

import java.util.List;

/**
 * <p>
 * This interface represent a general SQL named query. A concrete <code>DataAccessObject</code> realization
 * has a configured set of provided queries.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface Query<T> extends Statement {

    T executeAndFetchFirst() throws DataAccessException;

    List<T> execute() throws DataAccessException;

    Query<T> clone();
}
