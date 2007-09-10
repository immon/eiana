package org.iana.ticketing;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class MockTicketingService implements TicketingService {

    public long createTicket(String tld) throws TicketingException {
        System.out.println("#### createTicket: " + tld);
        throw new TicketingException(); 
    }

    public void setIanaState(long ticketId, String stateName) {
        System.out.println("#### setIanaState: " + ticketId + ", " + stateName);
    }

    public void closeTicket(long ticketId) throws TicketingException {
        System.out.println("#### closeTicket: " + ticketId);
    }
}
