package org.iana.rzm.web.common;

public class RzmApplicationException extends RuntimeException {

    public RzmApplicationException(Exception e) {
        super(e);
    }
}
