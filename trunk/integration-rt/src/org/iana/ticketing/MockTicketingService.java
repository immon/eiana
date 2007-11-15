package org.iana.ticketing;

import java.util.Date;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class MockTicketingService implements TicketingService {
    public long createTicket(Ticket ticket) throws TicketingException {
        System.out.println("#### createTicket: " + ticket.getTld());
        return new Date().getTime();
    }

    public void updateTicket(Ticket ticket) throws TicketingException {
        System.out.println("#### setIanaState: " + ticket.getId() + ", " + ticket.getIanaState());
    }

    public void closeTicket(Ticket ticket) throws TicketingException {
        System.out.println("#### closeTicket: " + ticket.getId());
    }
}
