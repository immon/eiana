package org.iana.dao;

/**
 * An implementation of Data Access Object pattern dedicated for the libraries with the transparent update.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DataAccessObject<T> {

    /**
     * Returns a persistent object with a given id.
     *
     * @param id an identifier of an object to be found
     * @return a persistent object if found, otherwise null
     * @throws DataAccessFailure
     */
    T get(long id) throws DataAccessFailure;

    /**
     * Returns a persistent object with a given id.
     *
     * @param id an identifier of an object to be found
     * @return a found persistent object
     * @throws DataAccessFailure
     * @throws NoSuchObjectException thrown when no object found for a given id
     */
    T load(long id) throws DataAccessFailure, NoSuchObjectException;

    void persist(T object) throws DataAccessFailure;

    void remove(long id) throws DataAccessFailure, NoSuchObjectException;

    Query<T> query(String name) throws NoSuchStatementException;

    Update<T> update(String name) throws NoSuchStatementException;
}
