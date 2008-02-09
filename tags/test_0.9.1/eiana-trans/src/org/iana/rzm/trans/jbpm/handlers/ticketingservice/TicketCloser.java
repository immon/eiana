package org.iana.rzm.trans.jbpm.handlers.ticketingservice;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.jbpm.handlers.ActionExceptionHandler;
import org.iana.ticketing.TicketingService;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class closes the ticket in the ticketing service, when the process reaches a terminal state.
 *
 * @author Jakub Laszkiewicz
 */
public class TicketCloser extends ActionExceptionHandler {
    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TicketingService ts = (TicketingService) executionContext.getJbpmContext().getObjectFactory().createObject("ticketingService");
        TransactionManager transactionManager = (TransactionManager)
                executionContext.getJbpmContext().getObjectFactory().createObject("transactionManagerBean");
        Transaction transaction = transactionManager.getTransaction(executionContext.getProcessInstance().getId());
        ts.closeTicket(new RequestTrackerTicket(transaction));
    }
}
