package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PendingImpactedPartiesDecision implements DecisionHandler {

    public String decide(ExecutionContext executionContext) throws Exception {
        return "iana";
    }
}
