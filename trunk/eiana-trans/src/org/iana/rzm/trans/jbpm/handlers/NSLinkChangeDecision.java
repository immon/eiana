package org.iana.rzm.trans.jbpm.handlers;

import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.trans.TransactionData;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public class NSLinkChangeDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if (td != null) {
            ObjectChange change = td.getDomainChange();
            if ((change != null)) {
                if (change.getFieldChanges().containsKey("nameServers"))
                    //todo    link must be checked
                    return "yes";
            }
        }
        return "no";
    }
}
