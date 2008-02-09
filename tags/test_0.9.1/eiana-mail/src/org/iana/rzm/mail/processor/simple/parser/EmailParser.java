package org.iana.rzm.mail.processor.simple.parser;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface EmailParser {

    MessageData parse(String subject, String content) throws EmailParseException;

}
