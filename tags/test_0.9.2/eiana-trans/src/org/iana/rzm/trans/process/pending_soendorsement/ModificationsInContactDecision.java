package org.iana.rzm.trans.process.pending_soendorsement;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.ctx.TransactionContext;
import org.iana.rzm.trans.process.general.handlers.DecisionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author: Piotr Tkaczyk
 */
public class ModificationsInContactDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        TransactionContext ctx = new TransactionContext(executionContext);
        Transaction trans = ctx.getTransaction();
        if (trans.isSupportingChange()) {
            return "yes";
        }
        return "no";
    }

}
