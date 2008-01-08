package org.iana.rzm.mail.processor.contact;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactAnswer implements MessageData {

    private long ticketID;

    private String domainName;

    private String token;

    private boolean accept;

    public ContactAnswer(long ticketID, String domainName, String token, boolean accept) {
        this.ticketID = ticketID;
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
