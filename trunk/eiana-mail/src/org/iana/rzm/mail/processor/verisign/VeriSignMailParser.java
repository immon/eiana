package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;

import java.text.ParseException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMailParser implements EmailParser {

    public static final String DOMAIN_NAME = "domain_name";

    private RegexParser subjectPattern;

    public VeriSignMailParser(RegexParser subjectPattern) {
        CheckTool.checkNull(subjectPattern, "subject pattern");
        this.subjectPattern = subjectPattern;
    }

    public MessageData parse(String from, String subject, String content) throws VerisignParseException {
        RegexParser.Tokens subjectTokens = parseSubject(subject);
        String domainName = subjectTokens.token(DOMAIN_NAME);
        return new VeriSignMail(domainName);
    }

    private RegexParser.Tokens parseSubject(String subject) throws VerisignParseException {
        try {
            return subjectPattern.parse(subject);
        } catch (ParseException e) {
            throw new VerisignParseException("Subject does not match contact confirmation subject pattern", e);
        }
    }

}
