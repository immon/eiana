package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.trans.technicalcheck.TechnicalCheckHelper;

/**
 * This class performs required technical checks and decides to which state a process
 * should be transited from the PENDING_TECH_CHECK state.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */
public class TechnicalCheck extends DecisionExceptionHandler {

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
