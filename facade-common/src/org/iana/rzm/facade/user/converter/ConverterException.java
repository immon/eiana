package org.iana.rzm.facade.user.converter;

/**
 * org.iana.rzm.facade.user.converter.ConverterException
 *
 * @author Marcin Zajaczkowski
 */
public class ConverterException extends Exception {


    public ConverterException() {
        super();
    }

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }
}
