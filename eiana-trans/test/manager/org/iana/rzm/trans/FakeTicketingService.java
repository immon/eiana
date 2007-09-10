package org.iana.rzm.trans;

import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;

/**
 * @author Lukasz Zuchowski
 * @author Jakub Laszkiewicz
 */
public class FakeTicketingService implements TicketingService {

    public long createTicket(String tld) throws TicketingException {
        throw new TicketingException();
    }

    public void setIanaState(long ticketId, String stateName) throws TicketingException {
    }

    public void closeTicket(long ticketId) throws TicketingException {
    }
}
