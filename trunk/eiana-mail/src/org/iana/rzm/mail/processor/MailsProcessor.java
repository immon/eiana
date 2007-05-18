package org.iana.rzm.mail.processor;

/**
 * @author Jakub Laszkiewicz
 */
public interface MailsProcessor {
    public void process(String from, String subject, String content) throws MailsProcessorException;
}
