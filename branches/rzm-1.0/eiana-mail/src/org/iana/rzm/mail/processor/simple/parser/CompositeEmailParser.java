package org.iana.rzm.mail.processor.simple.parser;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.simple.data.MessageData;

import java.util.List;

/**
 * Finds the first processor that is able to parse an email i.e. it does not throw EmailParseException.
 *
 * @author Patrycja Wegrzynowicz
 */
public class CompositeEmailParser implements EmailParser {

    private List<EmailParser> parsers;

    public CompositeEmailParser(List<EmailParser> parsers) {
        CheckTool.checkCollectionNull(parsers, "email parsers");
        this.parsers = parsers;
    }

    public MessageData parse(String from, String subject, String content) throws EmailParseException {
        for (EmailParser parser : parsers) {
            try {
                return parser.parse(from, subject, content);
            } catch (EmailParseException e) {
                // try next parser
            }
        }
        throw new EmailParseException();
    }

}
