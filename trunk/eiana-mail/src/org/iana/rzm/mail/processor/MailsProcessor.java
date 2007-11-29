package org.iana.rzm.mail.processor;

import javax.mail.internet.MimeMessage;

/**
 * @author Jakub Laszkiewicz
 */
public interface MailsProcessor {

    public void process(String from, String subject, String content) throws MailsProcessorException;

    public void process(MimeMessage message) throws MailsProcessorException;
    
}
