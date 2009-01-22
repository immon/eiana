package org.iana.rzm.trans.process.pending_tech_check_remedy;

import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class TechnicalCheckRemedy extends ActionExceptionHandler {
    String period;

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        if (technicalCheckHelper.check(executionContext, period))
            executionContext.leaveNode("accept");
    }
}
