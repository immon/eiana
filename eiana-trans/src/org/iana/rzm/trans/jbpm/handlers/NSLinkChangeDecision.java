package org.iana.rzm.trans.jbpm.handlers;

import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.trans.TransactionData;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

/**
 * @author Piotr Tkaczyk
 */
public class NSLinkChangeDecision implements DecisionHandler {

    boolean doTest;

    public String decide(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if (td != null) {
            ObjectChange change = td.getDomainChange();
            if ((change != null) && (doTest)) {
                if (change.getFieldChanges().containsKey("nameServers"))
                    //todo    link must be checked
                    return "yes";
            }
        }
        return "no";
    }
}
