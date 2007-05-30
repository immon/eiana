package org.iana.rzm.web;

public class RzmApplicationException extends RuntimeException {

    public RzmApplicationException(Exception e) {
        super(e);
    }
}
