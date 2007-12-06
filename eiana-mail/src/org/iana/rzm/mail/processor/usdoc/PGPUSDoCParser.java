package org.iana.rzm.mail.processor.usdoc;

import org.iana.pgp.PGPUtils;
import org.iana.pgp.PGPUtilsException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PGPUSDoCParser implements EmailParser {

    private EmailParser plainParser;

    private boolean pgp;

    public PGPUSDoCParser(EmailParser parser) {
        this(parser, true);
    }

    public PGPUSDoCParser(EmailParser parser, boolean pgp) {
        CheckTool.checkNull(parser, "usdoc plain parser");
        this.plainParser = parser;
        this.pgp = pgp;
    }

    public void setPgp(boolean pgp) {
        this.pgp = pgp;
    }

    public MessageData parse(String subject, String content) throws EmailParseException {
        if (pgp) {
            try {
                String plainContent = PGPUtils.getSignedMessageContent(content);
                USDoCAnswer answer = (USDoCAnswer) plainParser.parse(subject, plainContent);
                answer.setPgp(true);
                return answer;
            } catch (PGPUtilsException e) {
                throw new EmailParseException(e);
            }
        } else {
            return plainParser.parse(subject, content);
        }
    }
}
