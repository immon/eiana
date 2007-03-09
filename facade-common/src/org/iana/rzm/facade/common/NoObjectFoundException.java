package org.iana.rzm.facade.common;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NoObjectFoundException extends Exception {

    private long id;
    private String name;

    public NoObjectFoundException(long id) {
        this.id = id;
    }

    public NoObjectFoundException(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
