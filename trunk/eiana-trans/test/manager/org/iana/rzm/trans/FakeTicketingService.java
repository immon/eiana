package org.iana.rzm.trans;

import org.iana.ticketing.Ticket;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;

import java.util.Date;

/**
 * @author Lukasz Zuchowski
 * @author Jakub Laszkiewicz
 */
public class FakeTicketingService implements TicketingService {
    public long createTicket(Ticket ticket) throws TicketingException {
        return new Date().getTime();
    }

    public void updateTicket(Ticket ticket) throws TicketingException {
    }

    public void closeTicket(Ticket ticket) throws TicketingException {
    }

    public void addComment(long ticketID, String comment) throws TicketingException {

    }
}
