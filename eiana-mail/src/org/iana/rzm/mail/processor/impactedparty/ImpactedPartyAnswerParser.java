package org.iana.rzm.mail.processor.impactedparty;

import org.iana.rzm.mail.processor.contact.ContactAnswerParser;
import org.iana.rzm.mail.processor.regex.RegexParser;
import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartyAnswerParser extends ContactAnswerParser {
    public ImpactedPartyAnswerParser(RegexParser subjectPattern, RegexParser contentPattern, String acceptString, String declineString) {
        super(subjectPattern, contentPattern, acceptString, declineString);
    }

    protected MessageData createAnswer(long ticketID, String domainName, String token, boolean accept) {
        return new ImpactedPartyAnswer(ticketID, domainName, token, accept);
    }
}
