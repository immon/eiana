package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.parser.EmailParseException;

/**
 * @author Piotr Tkaczyk
 */
public class VerisignParseException extends EmailParseException {

    public VerisignParseException() {
    }

    public VerisignParseException(String message) {
        super(message);
    }

    public VerisignParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerisignParseException(Throwable cause) {
        super(cause);
    }
}
