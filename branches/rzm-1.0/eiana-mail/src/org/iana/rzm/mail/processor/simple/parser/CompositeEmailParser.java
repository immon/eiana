package org.iana.rzm.mail.processor.simple.parser;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Finds the first processor that is able to parse an email i.e. it does not throw EmailParseException.
 *
 * @author Patrycja Wegrzynowicz
 */
public class CompositeEmailParser implements EmailParser {

    private List<EmailParser> parsers;
    private static final Logger logger = Logger.getLogger(CompositeEmailParser.class.getName());

    public CompositeEmailParser(List<EmailParser> parsers) {
        CheckTool.checkCollectionNull(parsers, "email parsers");
        this.parsers = parsers;
    }

    public MessageData parse(String from, String subject, String content) throws EmailParseException {
        for (EmailParser parser : parsers) {
            try {
                return parser.parse(from, subject, content);
            } catch (EmailParseException e) {
                logger.debug("CompositeEmailParser: Cought EmailParserException  Moving to next parser ", e);
                // try next parser
            }
        }
        throw new EmailParseException();
    }

}
