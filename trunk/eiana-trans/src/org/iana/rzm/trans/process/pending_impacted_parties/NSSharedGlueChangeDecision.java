package org.iana.rzm.trans.process.pending_impacted_parties;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.handlers.DecisionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Set;

/**
 * @author: Piotr Tkaczyk
 */
public class NSSharedGlueChangeDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        Transaction trans = getTransaction(executionContext);
        Set<Domain> impactedDomains = trans.getImpactedDomains();
        return impactedDomains != null && !impactedDomains.isEmpty() ?
                "yes" : "no";
    }
}
