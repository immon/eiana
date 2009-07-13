package org.iana.rzm.mail.processor.simple.error;

import org.iana.rzm.mail.processor.simple.parser.EmailParseException;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface EmailErrorHandler {

    void error(String from, String subject, String content, String message);

    void error(String from, String subject, String content, Exception exception);

    void error(String from, String subject, String content, Exception exception, String template);

}
