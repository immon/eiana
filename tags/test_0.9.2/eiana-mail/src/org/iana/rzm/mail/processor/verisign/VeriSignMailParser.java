package org.iana.rzm.mail.processor.verisign;

import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AuthenticationService;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;

import java.text.ParseException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMailParser implements EmailParser {

    public static final String DOMAIN_NAME = "domain_name";

    private Config config;

    private RegexParser subjectPattern;

    public VeriSignMailParser(ParameterManager parameterManager, RegexParser subjectPattern) {
        CheckTool.checkNull(parameterManager, "parameter manager");
        CheckTool.checkNull(subjectPattern, "subject pattern");
        this.config = new OwnedConfig(parameterManager);
        this.subjectPattern = subjectPattern;
    }

    public MessageData parse(String from, String subject, String content) throws EmailParseException {
        try {
            String email = config.getParameter(AuthenticationService.VERISIGN_EMAIL);
            if (email == null) throw new EmailParseException("Verisign email address stored in db is null.");
            if (!email.equals(from)) throw new EmailParseException("The from address [" + from + "] does not match the verisign address [" + email + "]");
            RegexParser.Tokens subjectTokens = parseSubject(subject);
            String domainName = subjectTokens.token(DOMAIN_NAME);
            return new VeriSignMail(domainName);
        } catch (ConfigException e) {
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
