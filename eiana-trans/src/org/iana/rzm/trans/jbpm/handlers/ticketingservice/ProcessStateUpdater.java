package org.iana.rzm.trans.jbpm.handlers.ticketingservice;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.jbpm.handlers.ActionExceptionHandler;
import org.iana.ticketing.TicketingService;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class updates the ticket's "IANA State" in the ticketing service,
 * when process state changes.
 *
 * @author Jakub Laszkiewicz
 */
public class ProcessStateUpdater extends ActionExceptionHandler {
    public void doExecute(ExecutionContext executionContext) throws Exception {
        TicketingService ts = (TicketingService) executionContext.getJbpmContext().getObjectFactory().createObject("ticketingService");
        TransactionManager transactionManager = (TransactionManager)
                executionContext.getJbpmContext().getObjectFactory().createObject("transactionManagerBean");
        Transaction transaction = transactionManager.getTransaction(executionContext.getProcessInstance().getId());

        ts.updateTicket(new RequestTrackerTicket(transaction));
    }
}
