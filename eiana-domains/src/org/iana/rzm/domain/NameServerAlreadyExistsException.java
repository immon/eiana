package org.iana.rzm.domain;

public class NameServerAlreadyExistsException extends Exception {

    String name;

    public NameServerAlreadyExistsException(String name) {
        this.name = name;
    }
}
