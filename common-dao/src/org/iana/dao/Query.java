package org.iana.dao;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Query<T> extends Statement {

    T executeAndFetchFirst() throws DataAccessException;

    List<T> execute() throws DataAccessException;
}
