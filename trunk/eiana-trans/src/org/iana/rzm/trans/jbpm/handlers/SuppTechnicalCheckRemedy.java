package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author: Piotr Tkaczyk
 */
public class SuppTechnicalCheckRemedy extends ActionExceptionHandler {
    String period;

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        if (technicalCheckHelper.check(executionContext, period))
            executionContext.leaveNode("accept");
    }
}
