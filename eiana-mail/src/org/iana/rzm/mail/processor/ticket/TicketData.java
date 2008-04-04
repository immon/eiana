package org.iana.rzm.mail.processor.ticket;

import org.iana.rzm.mail.processor.simple.data.MessageData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TicketData implements MessageData {

    protected long ticketID;

    public TicketData(long ticketID) {
        this.ticketID = ticketID;
    }

    public long getTicketID() {
        return ticketID;
    }
}
