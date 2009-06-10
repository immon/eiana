package org.iana.rzm.mail.processor.simple;

import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AnswerParser {

    private String acceptString;

    private String declineString;

    public AnswerParser(String acceptString, String declineString) {
        CheckTool.checkNull(acceptString, "accept string");
        CheckTool.checkNull(declineString, "declien string");
        this.acceptString = acceptString.toLowerCase();
        this.declineString = declineString.toLowerCase();
    }

    public boolean check(String answer) throws EmailParseException {
        if (answer == null) throw new EmailParseException("null answer found");
        answer = answer.toLowerCase().trim();
        if (answer.equals(acceptString)) return true;
        if (answer.equals(declineString)) return false;
        if (answer.contains(acceptString) && answer.contains(declineString)) throw new EmailParseException("both answers present: [" + acceptString + "] and [" + declineString + "]");
        if (answer.contains(acceptString)) return true;
        if (answer.contains(declineString)) return false;
        throw new EmailParseException("unparseable answer provided: [" + answer + "]");
    }
}
