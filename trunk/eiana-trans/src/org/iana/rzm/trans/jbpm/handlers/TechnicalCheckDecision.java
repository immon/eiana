package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class performs required technical checks and decides to which state a process
 * should be transited from the PENDING_TECH_CHECK state.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TechnicalCheckDecision implements DecisionHandler {

    public String decide(ExecutionContext executionContext) throws Exception {
        return "no-test";
    }
}
