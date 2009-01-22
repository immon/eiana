package org.iana.rzm.mail.processor.simple.pgp;

import org.iana.pgp.PGPUtils;
import org.iana.pgp.PGPUtilsException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PGPParser implements EmailParser {
    private EmailParser delegate;

    private boolean pgp;

    public PGPParser(EmailParser parser) {
        this(parser, true);
    }

    public PGPParser(EmailParser parser, boolean pgp) {
        CheckTool.checkNull(parser, "email delegate parser");
        this.delegate = parser;
        this.pgp = pgp;
    }

    public void setPgp(boolean pgp) {
        this.pgp = pgp;
    }

    public MessageData parse(String from, String subject, String content) throws EmailParseException {
        if (pgp) {
            try {
                String plainContent = PGPUtils.getSignedMessageContent(content);
                PGPMessageData answer = (PGPMessageData) delegate.parse(from, subject, plainContent);
                answer.setPgp(true);
                return answer;
            } catch (PGPUtilsException e) {
                throw new EmailParseException(e);
            }
        } else {
            return delegate.parse(from, subject, content);
        }
    }
}

