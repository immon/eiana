package org.iana.rzm.common.exceptions;

public class InvalidNameException extends RuntimeException {

    String name;
    String reason;

    public InvalidNameException(String name, String reason) {
        this.name = name;
        this.reason = reason;
    }

    public String getName() {
        return this.name;
    }

    public String getReason() {
        return this.reason;
    }
}
