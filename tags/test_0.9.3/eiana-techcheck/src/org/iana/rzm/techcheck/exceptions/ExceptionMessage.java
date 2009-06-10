package org.iana.rzm.techcheck.exceptions;

/**
 * @author: Piotr Tkaczyk
 */

public class ExceptionMessage {

    private String owner;
    private String value;

    protected ExceptionMessage(String owner, String value) {
        this.owner = owner;
        this.value = value;
    }

    public String getOwner() {
        return owner;
    }

    public String getValue() {
        return value;
    }
}

