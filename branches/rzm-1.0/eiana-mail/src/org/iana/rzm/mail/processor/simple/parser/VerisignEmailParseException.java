package org.iana.rzm.mail.processor.simple.parser;

import org.iana.rzm.mail.processor.simple.parser.EmailParseException;

/**
 * @author Piotr Tkaczyk
 */
public class VerisignEmailParseException extends EmailParseException {

    public VerisignEmailParseException() {
    }

    public VerisignEmailParseException(String message) {
        super(message);
    }

    public VerisignEmailParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerisignEmailParseException(Throwable cause) {
        super(cause);
    }
}
