package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;
import org.iana.rzm.mail.processor.ticket.TicketData;

import java.text.ParseException;

/**
 * todo: recognize whether both decline and accept present
 *
 * @author Patrycja Wegrzynowicz
 */
public class ContactAnswerParser implements EmailParser {

    public static final String TICKET_ID = "ticket_id";

    public static final String DOMAIN_NAME = "domain_name";

    public static final String TOKEN = "token";

    public static final String ACCEPT = "accept";

    private RegexParser subjectPattern;

    private RegexParser contentPattern;

    private String acceptString;

    public ContactAnswerParser(RegexParser subjectPattern, RegexParser contentPattern, String acceptString) {
        CheckTool.checkNull(subjectPattern, "subject pattern");
        CheckTool.checkNull(contentPattern, "content pattern");
        CheckTool.checkNull(acceptString, "accept string");
        this.subjectPattern = subjectPattern;
        this.contentPattern = contentPattern;
        this.acceptString = acceptString;
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
                return createAnswer(ticketID, domainName, token, acceptString.equalsIgnoreCase(accept));
            } catch (EmailParseException e) {
                return new TicketData(ticketID);
            }
        } catch (NumberFormatException e) {
            throw new EmailParseException(e);
        }
    }

    protected MessageData createAnswer(long ticketID, String domainName, String token, boolean accept) {
        return new ContactAnswer(ticketID, domainName, token, accept);
    }

    private RegexParser.Tokens parseSubject(String subject) throws EmailParseException {
        try {
            return subjectPattern.parse(subject);
        } catch (ParseException e) {
            throw new EmailParseException("Subject does not match contact confirmation subject pattern", e);
        }
    }

    private RegexParser.Tokens parseContent(String content) throws EmailParseException {
        try {
            return contentPattern.parse(content);
        } catch (ParseException e) {
            throw new EmailParseException("Content does not match contact confirmation content pattern", e);
        }
    }

}
