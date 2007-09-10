package org.iana.ticketing.rt;

import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;
import org.iana.rt.RTStore;
import org.iana.rt.queue.Queue;
import org.iana.rt.ticket.Ticket;
import org.iana.codevalues.CodeValuesRetriever;

import java.io.IOException;

/**
 * <p>
 * This class is an implementation of <code>TicketingService</code> and facilitates the communication
 * with the RequestTracker, the ticketing system used by ICANN to process domain transactions.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class RequestTrackerService implements TicketingService {
    private static final String QUEUE_NAME = "IANA-Root_Mgmt";
    private static final String TICKET_SUBJECT = "Root Zone Change request for .%tld% (%label%)";
    private static final String CUSTOM_FIELD_IANA_STATE = "IANA State";

    private RTStore store;
    private CodeValuesRetriever retriever;

    public RequestTrackerService(String url, String username, String password) throws TicketingException {
        this(url, username, password, null);
    }

    public RequestTrackerService(String url, String username, String password, CodeValuesRetriever retriever) throws TicketingException {
        try {
            store = RTStore.getStore(url, username, password);
            this.retriever = retriever;
        } catch (IOException e) {
            throw new TicketingException("service creation failed");
        }
    }

    public long generateID() {
        return 0;
    }

    public long createTicket(String tld) throws TicketingException {
        try {
            Ticket ticket = store.tickets().newInstance();
            Queue queue = store.queues().findByName(QUEUE_NAME);
            if (queue == null) throw new TicketingException("Queue does not exist: " + QUEUE_NAME);
            ticket.setQueue(queue);
            ticket.setStatus(Ticket.Status.Open);
            ticket.setSubject(TICKET_SUBJECT.replace("%tld%", tld).replace("%label%", getLabel(tld)));
            store.tickets().create(ticket);
            return ticket.getId();
        } catch (IOException e) {
            throw new TicketingException(e);
        }
    }

    private String getLabel(String tld) {
        String label = null;
        if (retriever != null) label = retriever.getValueById("cc", tld);
        if (label == null) label = "";
        return label;
    }
    
    public void setIanaState(long ticketId, String stateName) throws TicketingException {
        try {
            Ticket ticket = store.tickets().load(ticketId);
            ticket.customFields().setSingleVal(CUSTOM_FIELD_IANA_STATE, stateName);
            store.tickets().update(ticket);
        } catch (IOException e) {
            throw new TicketingException(e);
        }
    }

    public void closeTicket(long ticketId) throws TicketingException {
        try {
            Ticket ticket = store.tickets().load(ticketId);
            ticket.setStatus(Ticket.Status.Resolved);
            store.tickets().update(ticket);
        } catch (IOException e) {
            throw new TicketingException(e);
        }
    }
}
