package org.iana.rzm.trans.process.pending_zone_insertion;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.ctx.TransactionContext;
import org.iana.rzm.trans.process.general.handlers.DecisionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class checks whether a process involves a name server change or not.
 *
 * @author Patrycja Wegrzynowicz
 */
public class NameServerChange extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        TransactionContext ctx = new TransactionContext(executionContext);
        Transaction trans = ctx.getTransaction();
        if (trans.isNameServerChange()) {
            return "ns-change";
        }
        return "no-ns-change";
    }
}
