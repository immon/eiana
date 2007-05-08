package org.iana.rzm.common.exceptions;

public class InvalidNameException extends RuntimeException {

    String name;

    public InvalidNameException(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
