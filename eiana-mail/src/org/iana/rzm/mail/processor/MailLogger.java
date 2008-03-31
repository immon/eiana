package org.iana.rzm.mail.processor;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface MailLogger {

    void logMail(long ticketID, String from, String subject, String body);

}
