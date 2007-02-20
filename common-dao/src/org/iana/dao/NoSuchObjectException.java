package org.iana.dao;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoSuchObjectException extends DataAccessException {

    private long id;

    public NoSuchObjectException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
