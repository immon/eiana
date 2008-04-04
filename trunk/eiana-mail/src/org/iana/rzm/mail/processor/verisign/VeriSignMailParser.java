package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;

import java.text.ParseException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMailParser implements EmailParser {

    public static final String TICKET_ID = "ticket_id";

    private String verisignAddress;

    private RegexParser subjectPattern;

    public VeriSignMailParser(String verisignAddress, RegexParser subjectPattern, RegexParser contentPattern) {
        CheckTool.checkNull(verisignAddress, "verisign address");
        CheckTool.checkNull(subjectPattern, "subject pattern");
        this.verisignAddress = verisignAddress;
        this.subjectPattern = subjectPattern;
    }

    public MessageData parse(String from, String subject, String content) throws EmailParseException {
        if (!verisignAddress.equals(from)) throw new EmailParseException("The from address [" + from + "] does not match the verisign address [" + verisignAddress + "]");
        try {
            RegexParser.Tokens subjectTokens = parseSubject(subject);
            long ticketID = Long.parseLong(subjectTokens.token(TICKET_ID));
            return new VeriSignMail(ticketID);
        } catch (NumberFormatException e) {
            throw new EmailParseException(e);
        }
    }

    private RegexParser.Tokens parseSubject(String subject) throws EmailParseException {
        try {
            return subjectPattern.parse(subject);
        } catch (ParseException e) {
            throw new EmailParseException("Subject does not match contact confirmation subject pattern", e);
        }
    }

}
