package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.ticket.TicketData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactAnswer extends TicketData implements MessageData {

    private String domainName;

    private String token;

    private boolean accept;

    public ContactAnswer(long ticketID, String domainName, String token, boolean accept) {
        super(ticketID);
        this.domainName = domainName;
        this.token = token;
        this.accept = accept;
    }

    public long getTicketID() {
        return ticketID;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getToken() {
        return token;
    }

    public boolean isAccept() {
        return accept;
    }

}
