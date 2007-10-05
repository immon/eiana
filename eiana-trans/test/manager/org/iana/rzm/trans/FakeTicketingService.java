package org.iana.rzm.trans;

import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.Ticket;

/**
 * @author Lukasz Zuchowski
 * @author Jakub Laszkiewicz
 */
public class FakeTicketingService implements TicketingService {
    public long createTicket(Ticket ticket) throws TicketingException {
        throw new TicketingException();
    }

    public void updateTicket(Ticket ticket) throws TicketingException {
    }

    public void closeTicket(Ticket ticket) throws TicketingException {
    }
}
