package org.iana.rzm.trans.jbpm.handlers.ticketingservice;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.ticketing.TicketingService;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.jbpm.handlers.ActionExceptionHandler;

/**
 * This class closes the ticket in the ticketing service, when the process reaches a terminal state.
 *
 * @author Jakub Laszkiewicz
 */
public class TicketCloser extends ActionExceptionHandler {
    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TicketingService ts = (TicketingService) executionContext.getJbpmContext().getObjectFactory().createObject("ticketingService");
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if (td.getTicketID() != null) ts.closeTicket(td.getTicketID());
    }
}
