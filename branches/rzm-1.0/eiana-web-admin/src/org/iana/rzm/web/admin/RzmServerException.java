package org.iana.rzm.web.admin;

public class RzmServerException extends Exception {
    public RzmServerException(String msg) {
        super(msg);
    }

    public RzmServerException(Exception e, String message) {
        super(message, e);
    }
}
