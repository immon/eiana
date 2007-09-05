package org.iana.ticketing.rt;

import org.iana.rt.RTStore;
import org.iana.rt.ticket.Ticket;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = "excluded")
public class RequestTrackerServiceTest {
    private static final String RTS_URL = "http://localhost:8284/";
    private static final String RTS_USERNAME = "root";
    private static final String RTS_PASSWORD = "password";

    private TicketingService rts;
    private RTStore store;

    @BeforeClass
    public void intit() throws TicketingException, IOException {
        rts = new RequestTrackerService(RTS_URL, RTS_USERNAME, RTS_PASSWORD);
        store = RTStore.getStore(RTS_URL, RTS_USERNAME, RTS_PASSWORD);
    }

    private static final String TICKET_NEW_TLD = "PL";
    private static final String TICKET_NEW_LABEL = "Poland";
    private static final String QUEUE_NAME = "IANA-Root_Mgmt";
    private static final String TICKET_NEW_SUBJECT = "Root Zone Change request for .PL (Poland)";

    public void createTicket() throws TicketingException, IOException {
        long ticketId = rts.createTicket(TICKET_NEW_TLD, TICKET_NEW_LABEL);

        Ticket ticket = store.tickets().load(ticketId);
        assert ticket.getQueue() != null : "queue is not set"; 
        assert QUEUE_NAME.equals(ticket.getQueue().getName()) : "unexpected queue name: " + ticket.getQueue().getName();
        assert Ticket.Status.Open.equals(ticket.getStatus()) : "unexpected status: " + ticket.getStatus();
        assert TICKET_NEW_SUBJECT.equals(ticket.getSubject()) : "unexpected subject: " + ticket.getSubject();
    }

    private static final String TICKET_STATE_TLD = "IE";
    private static final String TICKET_STATE_LABEL = "Ireland";
    private static final String TICKET_STATE_SUBJECT = "Root Zone Change request for .IE (Ireland)";
    private static final String TICKET_STATE_FIELD = "IANA State";
    private static final String TICKET_STATE_VALUE = "tech-check";

    public void setState() throws TicketingException, IOException {
        long ticketId = rts.createTicket(TICKET_STATE_TLD, TICKET_STATE_LABEL);
        rts.setIanaState(ticketId, TICKET_STATE_VALUE);

        Ticket ticket = store.tickets().load(ticketId);
        assert ticket.getQueue() != null : "queue is not set";
        assert QUEUE_NAME.equals(ticket.getQueue().getName()) : "unexpected queue name: " + ticket.getQueue().getName();
        assert Ticket.Status.Open.equals(ticket.getStatus()) : "unexpected status: " + ticket.getStatus();
        assert TICKET_STATE_SUBJECT.equals(ticket.getSubject()) : "unexpected subject: " + ticket.getSubject();
        assert TICKET_STATE_VALUE.equals(ticket.customFields().getSingleVal(TICKET_STATE_FIELD)) :
                "unexpected state: " + ticket.customFields().getSingleVal(TICKET_STATE_FIELD);
    }

    private static final String TICKET_CLOSE_TLD = "DE";
    private static final String TICKET_CLOSE_LABEL = "Germany";
    private static final String TICKET_CLOSE_SUBJECT = "Root Zone Change request for .DE (Germany)";

    public void closeTicket() throws TicketingException, IOException {
        long ticketId = rts.createTicket(TICKET_CLOSE_TLD, TICKET_CLOSE_LABEL);
        rts.closeTicket(ticketId);

        Ticket ticket = store.tickets().load(ticketId);
        assert ticket.getQueue() != null : "queue is not set";
        assert QUEUE_NAME.equals(ticket.getQueue().getName()) : "unexpected queue name: " + ticket.getQueue().getName();
        assert Ticket.Status.Resolved.equals(ticket.getStatus()) : "unexpected status: " + ticket.getStatus();
        assert TICKET_CLOSE_SUBJECT.equals(ticket.getSubject()) : "unexpected subject: " + ticket.getSubject();
    }
}
