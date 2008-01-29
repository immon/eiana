package org.iana.rzm.trans.jbpm.handlers.ticketingservice;

import org.iana.rzm.trans.jbpm.handlers.ActionExceptionHandler;
import org.iana.rzm.trans.TransactionData;
import org.iana.ticketing.TicketingService;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public class TicketExceptionStateAddComment extends ActionExceptionHandler {

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TicketingService ts = (TicketingService) executionContext.getJbpmContext().getObjectFactory().createObject("ticketingService");
        TransactionData td = (TransactionData) executionContext.getVariable("TRANSACTION_DATA");
        String s = td.getStateMessage();
        if (s != null && s.trim().length() != 0) {
            ts.addComment(td.getTicketID(), s);
        }
    }
}
