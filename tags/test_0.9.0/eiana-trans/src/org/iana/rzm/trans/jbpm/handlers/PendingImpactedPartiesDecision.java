package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PendingImpactedPartiesDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        return "iana";
    }
}
