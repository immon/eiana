package org.iana.rzm.mail.processor.impactedparty;

import org.iana.rzm.mail.processor.contact.ContactAnswer;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartyAnswer extends ContactAnswer {

    public ImpactedPartyAnswer(long ticketID, String domainName, String token, boolean accept) {
        super(ticketID, domainName, token, accept);
    }

}
