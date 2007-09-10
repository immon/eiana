package org.iana.rzm.trans.jbpm.handlers.ticketingservice;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.ticketing.TicketingService;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.jbpm.handlers.ActionExceptionHandler;

import java.util.Map;

/**
 * This class updates the ticket's "IANA State" in the ticketing service,
 * when process state changes.
 *
 * @author Jakub Laszkiewicz
 */
public class ProcessStateUpdater extends ActionExceptionHandler {
    Map<String, String> processStateToIanaStatus;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        TicketingService ts = (TicketingService) executionContext.getJbpmContext().getObjectFactory().createObject("ticketingService");
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        String ianaState = processStateToIanaStatus.get(executionContext.getProcessInstance().getRootToken().getNode().getName());
        if (ianaState != null && td.getTicketID() != null) {
            ts.setIanaState(td.getTicketID(), ianaState);
        }
    }
}
