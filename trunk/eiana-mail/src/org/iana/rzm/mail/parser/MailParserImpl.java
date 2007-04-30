package org.iana.rzm.mail.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class MailParserImpl implements MailParser {
    private static final String RESPONSE_PREFIX = "Re:";
    private static final String SUBJECT_SEPARATOR = "\\|";
    private static final String ACCEPT_STRING = "I ACCEPT";

    public MailData parse(String from, String subject, String content) throws MailParserException {
        String clearSubject = cleanSubject(subject);
        String[] elements = clearSubject.split(SUBJECT_SEPARATOR);
        if (elements.length != 2) {
            throw new MailParserException("wrong subject");
        }
        ConfirmationMailData result = new ConfirmationMailData();
        result.setEmail(from);
        result.setTicketId(Long.parseLong(elements[0].trim()));
        result.setStateName(elements[1].trim());
        result.setAccepted(content.contains(ACCEPT_STRING));
        return result;
    }

    private String cleanSubject(String msgSubject) {
        String result = msgSubject.trim();
        while (result.startsWith(RESPONSE_PREFIX)) {
            result = result.substring(RESPONSE_PREFIX.length()).trim();
        }
        return result;
    }
}
