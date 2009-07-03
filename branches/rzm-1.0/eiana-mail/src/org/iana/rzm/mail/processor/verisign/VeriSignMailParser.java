package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;
import org.apache.log4j.Logger;
import org.iana.rzm.mail.processor.simple.parser.VerisignEmailParseException;

import java.text.ParseException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMailParser implements EmailParser {

    private static final Logger LOGGER = Logger.getLogger(VeriSignMailParser.class.getName());

    public static final String DOMAIN_NAME = "domain_name";

    private String verisignAddress;

    private RegexParser subjectPattern;

    public VeriSignMailParser(String verisignAddress, RegexParser subjectPattern) {
        CheckTool.checkNull(verisignAddress, "verisign address");
        CheckTool.checkNull(subjectPattern, "subject pattern");
        this.verisignAddress = verisignAddress;
        this.subjectPattern = subjectPattern;
    }

    public MessageData parse(String from, String subject, String content) throws VerisignEmailParseException {
        StringBuilder builder = new StringBuilder("Cannot parse USDoC Email Content")
                .append("\n").append("From: ").append(from)
                .append("\n").append("Subject:").append(subject)
                .append("\n").append("Content: ").append(content);

        LOGGER.debug(builder.toString());
        if (!verisignAddress.equals(from)) {
            LOGGER.debug("The from address [" + from + "] does not match the verisign address [" + verisignAddress + "]");
            throw new VerisignEmailParseException("The from address [" + from + "] does not match the verisign address [" + verisignAddress + "]");
        }

        RegexParser.Tokens subjectTokens = parseSubject(subject);
        String domainName = subjectTokens.token(DOMAIN_NAME);
        return new VeriSignMail(domainName);
    }

    private RegexParser.Tokens parseSubject(String subject) throws VerisignEmailParseException {
        try {
            return subjectPattern.parse(subject);
        } catch (ParseException e) {
            LOGGER.warn("The Subject " + subject + " does not match contact confirmation subject pattern", e);
            throw new VerisignEmailParseException("Subject does not match contact confirmation subject pattern", e);
        }
    }

}
