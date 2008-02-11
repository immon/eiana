package org.iana.rzm.trans.process.general.ctx;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.Transaction;
import org.iana.ticketing.TicketingService;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionContext {

    protected ExecutionContext ctx;

    private ObjectFactory objectFactory;

    public TransactionContext(ExecutionContext ctx) {
        CheckTool.checkNull(ctx, "execution context");
        this.ctx = ctx;
    }

    public Transaction getTransaction() {
        return new Transaction(ctx.getProcessInstance());
    }

    public TicketingService getTicketingService() {
        return (TicketingService) getObjectFactory().createObject("ticketingService");    
    }

    protected ObjectFactory getObjectFactory() {
        if (objectFactory == null) {
            synchronized (this) {
                if (objectFactory == null) {
                    objectFactory = ctx.getJbpmContext().getObjectFactory();
                }
            }
        }
        return objectFactory;
    }
}
