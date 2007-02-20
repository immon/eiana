package org.iana.rzm.common.exceptions;

public class InvalidNameException extends Exception {

    String name;

    public InvalidNameException(String name) {
        this.name = name;
    }
}
