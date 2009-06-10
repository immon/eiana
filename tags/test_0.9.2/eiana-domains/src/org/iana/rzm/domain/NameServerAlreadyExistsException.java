package org.iana.rzm.domain;

public class NameServerAlreadyExistsException extends RuntimeException {

    String name;

    public NameServerAlreadyExistsException(String name) {
        this.name = name;
    }
}
