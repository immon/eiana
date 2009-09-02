package org.iana.rzm.mail.processor.contact;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.AnswerParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;

import java.text.ParseException;

/**
 *
 * @author Patrycja Wegrzynowicz
 */
public class ContactAnswerParser implements EmailParser {

    private static Logger logger = Logger.getLogger(ContactAnswerParser.class);

    public static final String TICKET_ID = "ticket_id";

    public static final String DOMAIN_NAME = "domain_name";

    public static final String TOKEN = "token";

    public static final String ACCEPT = "accept";

    public static final String ERROR_TEMPLATE = "contact-parser-error";

    private RegexParser subjectPattern;

    private RegexParser contentPattern;

    private AnswerParser answerParser;

    public ContactAnswerParser(RegexParser subjectPattern, RegexParser contentPattern, String acceptString, String declineString) {
        CheckTool.checkNull(subjectPattern, "subject pattern");
        CheckTool.checkNull(contentPattern, "content pattern");
        CheckTool.checkNull(acceptString, "accept string");
        CheckTool.checkNull(declineString, "decline string");
        this.subjectPattern = subjectPattern;
        this.contentPattern = contentPattern;
        this.answerParser = new AnswerParser(acceptString, declineString);
    }

    public MessageData parse(String from, String subject, String content) throws EmailParseException {
        try {
            RegexParser.Tokens subjectTokens = parseSubject(subject);
            long ticketID = Long.parseLong(subjectTokens.token(TICKET_ID));
            try {
                RegexParser.Tokens contentTokens = parseContent(content);
                String domainName = subjectTokens.token(DOMAIN_NAME);
                String token = subjectTokens.token(TOKEN);
                String accept = contentTokens.token(ACCEPT);
                return createAnswer(ticketID, domainName, token, answerParser.check(accept));
            } catch (EmailParseException e) {
                throw new ContactMessageParseException("cannot parse contact/impacted party email content", e, ticketID);
            }
        } catch (NumberFormatException e) {
            throw new ContactMessageParseException(e);
        }
    }

    protected MessageData createAnswer(long ticketID, String domainName, String token, boolean accept) {
        return new ContactAnswer(ticketID, domainName, token, accept);
    }

    private RegexParser.Tokens parseSubject(String subject) throws EmailParseException {
        try {
            return subjectPattern.parse(subject);
        } catch (ParseException e) {
            throw new ContactParseException("Subject does not match contact confirmation subject pattern", e);
        }
    }

    private RegexParser.Tokens parseContent(String content) throws EmailParseException {
        try {
            return contentPattern.parse(content);
        } catch (ParseException e) {
            throw new ContactParseException("Content does not match contact confirmation content pattern", e);
        }
    }

}
