package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.trans.technicalcheck.TechnicalCheckHelper;

/**
 * @author: Piotr Tkaczyk
 */
public class SuppTechnicalCheck extends DecisionExceptionHandler {

    boolean doTest;
    String period;

    public String doDecide(ExecutionContext executionContext) throws Exception {
        if (doTest) {
            if (!TechnicalCheckHelper.check(executionContext, period))
                return "error";    
        }
        return "test-ok";
    }
}
