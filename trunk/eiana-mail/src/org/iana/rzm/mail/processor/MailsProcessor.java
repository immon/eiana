package org.iana.rzm.mail.processor;

/**
 * @author Jakub Laszkiewicz
 */
public interface MailsProcessor {
    public void process() throws MailsProcessorException;
}
