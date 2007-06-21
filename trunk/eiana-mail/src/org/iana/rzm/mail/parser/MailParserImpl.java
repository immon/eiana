package org.iana.rzm.mail.parser;

import org.iana.templates.TemplatesService;
import org.iana.templates.TemplatesServiceException;
import org.iana.templates.inst.SectionInst;

/**
 * @author Jakub Laszkiewicz
 */
public class MailParserImpl implements MailParser {
    private static final String RESPONSE_PREFIX = "Re:";
    private static final String SUBJECT_SEPARATOR = "\\|";
    private static final String ACCEPT_STRING = "I ACCEPT";
    private static final String DECLINE_STRING = "I DECLINE";

    private TemplatesService templatesService;

    public MailParserImpl(TemplatesService templatesService) {
        this.templatesService = templatesService;
    }

    public MailData parse(String subject, String content) throws MailParserException {
        try {
            String clearSubject = cleanSubject(subject);
            String[] elements = clearSubject.split(SUBJECT_SEPARATOR);
            switch (elements.length) {
                case 1:
                    SectionInst template = templatesService.parseTemplate(content);
                    return new TemplateMailData(clearSubject, content, template);
                case 5:
                    ConfirmationMailData result = new ConfirmationMailData(clearSubject, content);
                    result.setTransactionId(Long.parseLong(elements[0].trim()));
                    result.setStateName(elements[1].trim());
                    result.setDomainName(elements[3].trim());
                    result.setToken(elements[4].trim());
                    content = content.toUpperCase();
                    String firstLine = getFirstLine(content);
                    if (firstLine.contains(ACCEPT_STRING) && firstLine.contains(DECLINE_STRING))
                        throw new MailParserException("both accept and decline are present");
                    if (!firstLine.contains(ACCEPT_STRING) && !firstLine.contains(DECLINE_STRING))
                        throw new MailParserException("both accept and decline are missing");
                    result.setAccepted(firstLine.contains(ACCEPT_STRING));
                    return result;
                default:
                    throw new MailParserException("wrong subject");
            }
        } catch (TemplatesServiceException e) {
            throw new MailParserTemplateException(e.getMessage(), e);
        }
    }

    private String getFirstLine(String content) {
        String [] splitted = content.split("\n|\r", 2);
        return splitted[0];
    }

    private String cleanSubject(String msgSubject) {
        String result = msgSubject.trim();
        while (result.startsWith(RESPONSE_PREFIX)) {
            result = result.substring(RESPONSE_PREFIX.length()).trim();
        }
        return result;
    }
}
