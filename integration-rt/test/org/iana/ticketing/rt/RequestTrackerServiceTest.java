package org.iana.ticketing.rt;

import org.iana.rt.RTException;
import org.iana.rt.RTStore;
import org.iana.rt.ticket.Ticket;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = "excluded")
public class RequestTrackerServiceTest {
    private static final String RTS_URL = "http://localhost:8284/";
    private static final String RTS_USERNAME = "root";
    private static final String RTS_PASSWORD = "password";

    private static final String CUSTOM_FIELD_IANA_STATE = "IANA State";
    private static final String CUSTOM_FIELD_TLD = "TLD";
    private static final String CUSTOM_FIELD_REQUEST_TYPE = "Request Type";

    private TicketingService rts;
    private RTStore store;

    @BeforeClass
    public void intit() throws TicketingException, IOException, RTException {
        rts = new RequestTrackerService(RTS_URL, RTS_USERNAME, RTS_PASSWORD, new CountryCodesRetriever());
        store = RTStore.getStore(RTS_URL, RTS_USERNAME, RTS_PASSWORD);
    }

    private static final String TICKET_NEW_TLD = "PL";
    private static final String TICKET_NEW_REQ_TYPE_1 = "ns";
    private static final String TICKET_NEW_REQ_TYPE_2 = "ac-data";
    private static final String QUEUE_NAME = "IANA-Root_Mgmt";
    private static final String TICKET_NEW_SUBJECT = "Root Zone Change request for .PL (Poland)";

    public void createTicket() throws TicketingException, IOException, RTException {
        TestTicket testTicket = new TestTicket(TICKET_NEW_TLD);
        testTicket.setRequestType(Arrays.asList(TICKET_NEW_REQ_TYPE_1, TICKET_NEW_REQ_TYPE_2));
        long ticketId = rts.createTicket(testTicket);

        Ticket ticket = store.tickets().load(ticketId);
        assert ticket.getQueue() != null : "queue is not set";
        assert QUEUE_NAME.equals(ticket.getQueue().getName()) : "unexpected queue name: " + ticket.getQueue().getName();
        assert Ticket.Status.Open.equals(ticket.getStatus()) : "unexpected status: " + ticket.getStatus();
        assert TICKET_NEW_SUBJECT.equals(ticket.getSubject()) : "unexpected subject: " + ticket.getSubject();
        assert Arrays.asList(TICKET_NEW_REQ_TYPE_1, TICKET_NEW_REQ_TYPE_2).equals(ticket.customFields().getMultiVal(CUSTOM_FIELD_REQUEST_TYPE)) :
                "unexpected request type: " + ticket.customFields().getMultiVal(CUSTOM_FIELD_REQUEST_TYPE);
        assert TICKET_NEW_TLD.equals(ticket.customFields().getSingleVal(CUSTOM_FIELD_TLD)) :
                "unexpected tld: " + ticket.customFields().getMultiVal(CUSTOM_FIELD_TLD);
    }

    private static final String TICKET_STATE_TLD = "IE";
    private static final String TICKET_STATE_SUBJECT = "Root Zone Change request for .IE (Ireland)";
    private static final String TICKET_STATE_VALUE = "tech-check";

    public void setState() throws TicketingException, IOException, RTException {
        TestTicket testTicket = new TestTicket(TICKET_STATE_TLD);
        testTicket.setIanaState(TICKET_STATE_VALUE);
        long ticketId = rts.createTicket(testTicket);
        testTicket.setId(ticketId);
        rts.updateTicket(testTicket);

        Ticket ticket = store.tickets().load(ticketId);
        assert ticket.getQueue() != null : "queue is not set";
        assert QUEUE_NAME.equals(ticket.getQueue().getName()) : "unexpected queue name: " + ticket.getQueue().getName();
        assert Ticket.Status.Open.equals(ticket.getStatus()) : "unexpected status: " + ticket.getStatus();
        assert TICKET_STATE_SUBJECT.equals(ticket.getSubject()) : "unexpected subject: " + ticket.getSubject();
        assert TICKET_STATE_VALUE.equals(ticket.customFields().getSingleVal(CUSTOM_FIELD_IANA_STATE)) :
                "unexpected state: " + ticket.customFields().getSingleVal(CUSTOM_FIELD_IANA_STATE);
    }

    private static final String TICKET_CLOSE_TLD = "DE";
    private static final String TICKET_CLOSE_SUBJECT = "Root Zone Change request for .DE (Germany)";

    public void closeTicket() throws TicketingException, IOException, RTException {
        TestTicket testTicket = new TestTicket(TICKET_CLOSE_TLD);
        long ticketId = rts.createTicket(testTicket);
        testTicket.setId(ticketId);
        rts.closeTicket(testTicket);

        Ticket ticket = store.tickets().load(ticketId);
        assert ticket.getQueue() != null : "queue is not set";
        assert QUEUE_NAME.equals(ticket.getQueue().getName()) : "unexpected queue name: " + ticket.getQueue().getName();
        assert Ticket.Status.Resolved.equals(ticket.getStatus()) : "unexpected status: " + ticket.getStatus();
        assert TICKET_CLOSE_SUBJECT.equals(ticket.getSubject()) : "unexpected subject: " + ticket.getSubject();
    }
}
