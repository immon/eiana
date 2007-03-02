package org.iana.dao;

/**
 * <p>
 * This exception is thrown when attempting to access an object that does not exist.
 * </p> 
 *
 * @author Patrycja Wegrzynowicz
 */
public class NoSuchObjectException extends DataAccessException {

    private Class clazz;
    private long id;

    public NoSuchObjectException(Class clazz, long id) {
        this.clazz = clazz;
        this.id = id;
    }

    public Class getClazz() {
        return clazz;
    }

    public long getId() {
        return id;
    }
}
