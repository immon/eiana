package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.trans.technicalcheck.TechnicalCheckHelper;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class TechnicalCheckRemedy extends ActionExceptionHandler {
    String period;

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        if (TechnicalCheckHelper.check(executionContext, period))
                executionContext.leaveNode("accept");
    }
}