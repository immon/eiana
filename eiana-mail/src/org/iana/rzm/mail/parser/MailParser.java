package org.iana.rzm.mail.parser;

/**
 * @author Jakub Laszkiewicz
 */
public interface MailParser {
    MailData parse(String subject, String content) throws MailParserException;
}
