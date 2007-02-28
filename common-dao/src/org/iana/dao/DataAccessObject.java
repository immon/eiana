package org.iana.dao;

/**
 * <p>
 * An implementation of Data Access Object pattern dedicated for the libraries with the transparent update.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface DataAccessObject<T> {

    /**
     * Returns a persistent object with a given id.
     *
     * @param id the identifier of an object to be found
     * @return the persistent object if found, otherwise null
     * @throws DataAccessFailure
     */
    T get(long id) throws DataAccessFailure;

    /**
     * Returns a persistent object with a given id.
     *
     * @param id the identifier of an object to be found
     * @return the found persistent object
     * @throws DataAccessFailure
     * @throws NoSuchObjectException thrown when no object found for the given id
     */
    T load(long id) throws DataAccessFailure, NoSuchObjectException;

    /**
     * Creates or explicitely updates a given object.
     *
     * @param object the object to be persisted
     * @throws DataAccessFailure
     */
    void persist(T object) throws DataAccessFailure;

    /**
     * Removes a given object from a persistent store.
     *
     * @param object the object to be removed
     * @throws DataAccessFailure
     */
    void remove(T object) throws DataAccessFailure;

    /**
     * Returns a pre-configured query for this data access object.
     * The parameters may be set if needed on the query and then the query may be executed.
     *
     * @param name the name of the query to be returned
     * @return the returned query
     * @throws NoSuchStatementException thrown when no query found for the given name
     */
    Query<T> query(String name) throws NoSuchStatementException;

    /**
     * Returns a pre-configured update for this data access object.
     * The parameters may be set if needed on the update and then the update may be executed.
     *
     * @param name the name of the update to be returned
     * @return the returned update
     * @throws NoSuchStatementException thrown when no update found for the given name
     */
    Update<T> update(String name) throws NoSuchStatementException;
}
