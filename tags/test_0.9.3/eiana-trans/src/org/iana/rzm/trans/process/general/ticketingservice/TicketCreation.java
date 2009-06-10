package org.iana.rzm.trans.process.general.ticketingservice;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.process.general.handlers.DecisionExceptionHandler;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * It's a decision node handler that tries
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class TicketCreation extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        try {
            TicketingService ts = (TicketingService) executionContext.getJbpmContext().getObjectFactory().createObject("ticketingService");
            TransactionManager transactionManager = (TransactionManager)
                    executionContext.getJbpmContext().getObjectFactory().createObject("transactionManagerBean");
            Transaction transaction = transactionManager.getTransaction(executionContext.getProcessInstance().getId());
            long ticketId = ts.createTicket(new RequestTrackerTicket(transaction));
            transaction.setTicketID(ticketId);
            return "ok";
        } catch (TicketingException e) {
            return "failed";
        }
    }
}
