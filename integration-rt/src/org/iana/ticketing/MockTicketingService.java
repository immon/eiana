package org.iana.ticketing;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class MockTicketingService implements TicketingService {

    public long generateID() {
        return 0;
    }

    public long createTicket(String tld, String label) throws TicketingException {
        System.out.println("#### createTicket: " + tld + ", " + label);
        return 0;
    }

    public void setIanaState(long ticketId, String stateName) {
        System.out.println("#### setIanaState: " + ticketId + ", " + stateName);
    }

    public void closeTicket(long ticketId) throws TicketingException {
        System.out.println("#### closeTicket: " + ticketId);
    }
}
