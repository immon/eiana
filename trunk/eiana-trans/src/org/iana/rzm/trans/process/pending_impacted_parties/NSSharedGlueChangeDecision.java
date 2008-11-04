package org.iana.rzm.trans.process.pending_impacted_parties;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.handlers.DecisionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author: Piotr Tkaczyk
 */
public class NSSharedGlueChangeDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        Transaction trans = getTransaction(executionContext);
        return trans.isNSSharedGlueChange() ? "yes" : "no";
    }
}
