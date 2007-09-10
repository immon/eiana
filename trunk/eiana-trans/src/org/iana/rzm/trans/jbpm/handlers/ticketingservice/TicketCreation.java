package org.iana.rzm.trans.jbpm.handlers.ticketingservice;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.jbpm.handlers.ActionExceptionHandler;
import org.iana.rzm.trans.jbpm.handlers.DecisionExceptionHandler;

/**
 * It's a decision node handler that tries
 * @author Patrycja Wegrzynowicz
 */
public class TicketCreation extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        try {
            TicketingService ts = (TicketingService) executionContext.getJbpmContext().getObjectFactory().createObject("ticketingService");
            TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
            long ticketId = ts.createTicket(td.getCurrentDomain().getName());
            td.setTicketID(ticketId);
            return "ok";
        } catch (TicketingException e) {
            return "failed";
        }
    }
}
