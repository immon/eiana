package org.iana.rzm.mail.processor.usdoc;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;
import org.iana.rzm.mail.processor.simple.AnswerParser;
import org.iana.rzm.mail.processor.ticket.TicketData;
import org.apache.log4j.Logger;

import java.text.ParseException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class USDoCAnswerParser implements EmailParser {

    private static Logger logger = Logger.getLogger(USDoCAnswerParser.class);

    public static final String TICKET_ID = "ticket_id";

    public static final String EPP_ID = "epp_id";

    public static final String DOMAIN_NAME = "domain_name";

    public static final String CHANGE_SUMMARY = "change_summary";

    public static final String ACCEPT = "accept";

    private RegexParser nsChangePattern;

    private RegexParser databaseChangePattern;

    private RegexParser contentPattern;

    private AnswerParser answerParser;

    public USDoCAnswerParser(RegexParser nsChangePattern, RegexParser databaseChangePattern, RegexParser contentPattern, String acceptString, String declineString) {
        CheckTool.checkNull(nsChangePattern, "ns change pattern");
        CheckTool.checkNull(databaseChangePattern, "database change pattern");
        CheckTool.checkNull(contentPattern, "content pattern");
        this.nsChangePattern = nsChangePattern;
        this.databaseChangePattern = databaseChangePattern;
        this.contentPattern = contentPattern;
        this.answerParser = new AnswerParser(acceptString, declineString);
    }

    public MessageData parse(String from, String subject, String content) throws EmailParseException {
        try {
            Object[] tokens = parseSubject(subject);
            boolean nameserver = (Boolean) tokens[1];

            RegexParser.Tokens subjectTokens = (RegexParser.Tokens) tokens[0];
            long ticketID = Long.parseLong(subjectTokens.token(TICKET_ID));

            try {
                RegexParser.Tokens contentTokens = parseContent(content);
                String eppID = subjectTokens.token(EPP_ID);
                String changeSummary = contentTokens.token(CHANGE_SUMMARY);
                String accept = contentTokens.token(ACCEPT);
                return new USDoCAnswer(ticketID, eppID, changeSummary, answerParser.check(accept), nameserver);
            } catch (EmailParseException e) {
                logger.error("cannot parse USDoC email content", e);
                return new TicketData(ticketID);
            }
        } catch (NumberFormatException e) {
            throw new EmailParseException(e);
        }
    }

    private Object[] parseSubject(String subject) throws EmailParseException {
        try {
            return new Object[]{databaseChangePattern.parse(subject), Boolean.FALSE};
        } catch (ParseException e) {
            try {
                return new Object[]{nsChangePattern.parse(subject), Boolean.TRUE};
            } catch (ParseException e1) {
                throw new EmailParseException("Subject does not match any USDoC subject pattern");
            }
        }
    }

    private RegexParser.Tokens parseContent(String content) throws EmailParseException {
        try {
            return contentPattern.parse(content);
        } catch (ParseException e) {
            throw new EmailParseException("Content does not match USDoC content pattern");
        }
    }

}
