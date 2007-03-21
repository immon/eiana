package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class checks whether a process involves a name server change or not.
 *
 * @author Patrycja Wegrzynowicz
 */
public class NameServerChange implements DecisionHandler {

    public String decide(ExecutionContext executionContext) throws Exception {
        return "no-ns-change";
    }
}
