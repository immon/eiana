package org.iana.rzm.mail.processor.simple.error;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface EmailErrorHandler {

    void error(String from, String subject, String content, Exception e);
}
