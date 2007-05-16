package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */

public class DomainException extends Exception {

    private String owner;
    private String value;

    public DomainException(String owner, String value) {
        this.owner = owner;
        this.value = value;
    }

    public String getOwner() {
        return owner;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return DomainException.class.getSimpleName();
    }

}
