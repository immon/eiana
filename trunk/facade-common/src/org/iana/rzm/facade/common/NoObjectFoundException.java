package org.iana.rzm.facade.common;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoObjectFoundException extends Exception {

    private long id;

    public NoObjectFoundException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
