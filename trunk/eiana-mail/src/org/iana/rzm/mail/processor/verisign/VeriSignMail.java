package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMail implements MessageData {

    long ticketID;

    public VeriSignMail(long ticketID) {
        this.ticketID = ticketID;
    }

    public long getTicketID() {
        return ticketID;
    }
}
