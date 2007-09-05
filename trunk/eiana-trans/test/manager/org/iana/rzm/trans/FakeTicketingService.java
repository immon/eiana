package org.iana.rzm.trans;

import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;

/**
 * @author Lukasz Zuchowski
 * @author Jakub Laszkiewicz
 */
public class FakeTicketingService implements TicketingService {
    public long generateID() {
        return 0;  
    }

    public long createTicket(String tld, String label) throws TicketingException {
        return 0;
    }

    public void setIanaState(long ticketId, String stateName) throws TicketingException {
    }

    public void closeTicket(long ticketId) throws TicketingException {
    }
}
