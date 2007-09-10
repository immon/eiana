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

    public long createTicket(String tld) throws TicketingException;

    public void setIanaState(long ticketId, String stateName) throws TicketingException;

    public void closeTicket(long ticketId) throws TicketingException;
}
