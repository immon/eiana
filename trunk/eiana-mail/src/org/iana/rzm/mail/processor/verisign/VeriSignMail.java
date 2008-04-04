package org.iana.rzm.mail.processor.verisign;

import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.ticket.TicketData;

/**
 * @author Patrycja Wegrzynowicz
 */
public class VeriSignMail extends TicketData implements MessageData {

    public VeriSignMail(long ticketID) {
        super(ticketID);
    }
}
