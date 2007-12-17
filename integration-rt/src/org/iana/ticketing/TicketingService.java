package org.iana.ticketing;

/**
 * <p>
 * This interface serves as a boundary interface to hide the communication details with the ticketing system
 * system used internally by ICANN to process domain modification transactions.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface TicketingService {

    public long createTicket(Ticket ticket) throws TicketingException;

    public void updateTicket(Ticket ticket) throws TicketingException;

    public void addComment(long ticketID, String comment) throws TicketingException;

    public void closeTicket(Ticket ticket) throws TicketingException;
}
