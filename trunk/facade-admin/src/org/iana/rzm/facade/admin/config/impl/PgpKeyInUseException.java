package org.iana.rzm.facade.admin.config.impl;

/**
 * @author Piotr Tkaczyk
 */
public class PgpKeyInUseException extends Exception {

    public PgpKeyInUseException() {
    }

    public PgpKeyInUseException(String string) {
        super(string);
    }

    public PgpKeyInUseException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public PgpKeyInUseException(Throwable throwable) {
        super(throwable);
    }
}
