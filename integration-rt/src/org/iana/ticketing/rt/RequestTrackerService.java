package org.iana.ticketing.rt;

import org.iana.codevalues.CodeValuesRetriever;
import org.iana.rt.RTException;
import org.iana.rt.RTStore;
import org.iana.rt.queue.Queue;
import org.iana.rt.ticket.Comment;
import org.iana.rt.ticket.Ticket;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * This class is an implementation of <code>TicketingService</code> and facilitates the communication
 * with the RequestTracker, the ticketing system used by ICANN to process domain transactions.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 * @author Piotr Tkaczyk
 */
public class RequestTrackerService implements TicketingService {
    private static final String QUEUE_NAME = "IANA-Root-Mgmt";
    private static final String TICKET_SUBJECT = "Root Zone Change request for .%tld% (%label%)";
    private static final String CUSTOM_FIELD_IANA_STATE = "IANA State";
    private static final String CUSTOM_FIELD_TLD = "TLD";
    private static final String CUSTOM_FIELD_REQUEST_TYPE = "Request Type";

    private static final String GLUE_CHANGE = "Glue change. \n";

    private RTStore store;
    private CodeValuesRetriever retriever;
    private Map<String, String> customFields;

    public RequestTrackerService(String url, String username, String password) throws TicketingException {
        this(url, username, password, null);
    }

    public RequestTrackerService(String url, String username, String password, CodeValuesRetriever retriever) throws TicketingException {
        try {
            store = RTStore.getStore(url, username, password);
            this.retriever = retriever;
            customFields = new HashMap<String, String>();
            customFields.put("state", CUSTOM_FIELD_IANA_STATE);
            customFields.put("tld", CUSTOM_FIELD_TLD);
            customFields.put("type", CUSTOM_FIELD_REQUEST_TYPE);
        } catch (RTException e) {
            throw new TicketingException("service creation failed", e);
        } catch (IOException e) {
            throw new TicketingException("service creation failed", e);
        }
    }

    public void setCustomFieldsKeys(Map fields) {
        customFields.put("tld", (String) fields.get("tld"));
        customFields.put("state", (String) fields.get("state"));
        customFields.put("type", (String) fields.get("type"));
    }

    public long generateID() {
        return 0;
    }

    public long createTicket(org.iana.ticketing.Ticket ticket) throws TicketingException {
        try {
            Ticket rtTicket = store.tickets().newInstance();
            Queue queue = store.queues().findByName(QUEUE_NAME);
            if (queue == null) throw new TicketingException("Queue does not exist: " + QUEUE_NAME);
            rtTicket.setQueue(queue);
            rtTicket.setStatus(Ticket.Status.Open);
            rtTicket.setSubject(TICKET_SUBJECT.replace("%tld%", ticket.getTld()).replace("%label%", getLabel(ticket.getTld())));
            //rtTicket.customFields().setSingleVal(CUSTOM_FIELD_TLD, ticket.getTld());
            //rtTicket.customFields().setMultiVal(CUSTOM_FIELD_REQUEST_TYPE, ticket.getRequestType());
            rtTicket.customFields().setSingleVal(customFields.get("tld"), ticket.getTld());
            store.tickets().create(rtTicket);
            String content = convertNewLines(ticket.getComment());
            Comment comment = store.tickets().commentFactory().create(content);
            store.tickets().addComment(rtTicket, comment);

            String currentState = convertNewLines(ticket.getCurrentStateComment());
            Comment currentStateComment = store.tickets().commentFactory().create(currentState);
            store.tickets().addComment(rtTicket, currentStateComment);

            if (ticket.isNSGlueTicket()) {
                Comment glueComment = store.tickets().commentFactory().create(GLUE_CHANGE);
                store.tickets().addComment(rtTicket, glueComment);
            }

            return rtTicket.getId();
        } catch (IOException e) {
            throw new TicketingException(e);
        } catch (RTException e) {
            throw new TicketingException(e);
        }
    }

    // todo: temporary solution of rt-lib bug
    private String convertNewLines(String s) {
        return s.replaceAll("\n", "\n\t");
    }

    public void updateTicket(org.iana.ticketing.Ticket ticket) throws TicketingException {
        if (ticket.getId() == null) return;
        try {
            Ticket rtTicket = store.tickets().load(ticket.getId());
            String ianaState = ticket.getIanaState();
            if (ianaState != null) {
                //rtTicket.customFields().setSingleVal(CUSTOM_FIELD_IANA_STATE, ianaState);
                rtTicket.customFields().setSingleVal(customFields.get("state"), ianaState);
                store.tickets().update(rtTicket);
            }
        } catch (IOException e) {
            throw new TicketingException(e);
        } catch (RTException e) {
            throw new TicketingException(e);
        }
    }

    public void closeTicket(org.iana.ticketing.Ticket ticket) throws TicketingException {
        if (ticket.getId() == null) return;
        try {
            Ticket rtTicket = store.tickets().load(ticket.getId());
            rtTicket.setStatus(Ticket.Status.Resolved);

            String currentState = convertNewLines(ticket.getCurrentStateComment());
            Comment currentStateComment = store.tickets().commentFactory().create(currentState);
            store.tickets().addComment(rtTicket, currentStateComment);
            
            store.tickets().update(rtTicket);
        } catch (IOException e) {
            throw new TicketingException(e);
        } catch (RTException e) {
            throw new TicketingException(e);
        }
    }

    private String getLabel(String tld) {
        String label = null;
        if (retriever != null) label = retriever.getValueById("cc", tld.toUpperCase());
        if (label == null) label = "";
        return label;
    }


    public void addComment(long ticketID, String msg) throws TicketingException {
        try {
            Ticket ticket = store.tickets().load(ticketID);
            Comment comment = store.tickets().commentFactory().create(msg);
            store.tickets().addComment(ticket, comment);
        } catch (IOException e) {
            throw new TicketingException(e);
        } catch (RTException e) {
            throw new TicketingException(e);
        }
    }
}
