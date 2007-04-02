package org.iana.rzm.facade.common;

/**
 * Thrown when no object found with a corresponding id or name.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class NoObjectFoundException extends Exception {

    private long id;
    private String name;
    private String type;

    public NoObjectFoundException(long id, String type) {
        this.id = id;
        this.type = type;
    }

    public NoObjectFoundException(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
