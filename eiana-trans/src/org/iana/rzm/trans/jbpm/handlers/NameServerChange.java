package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.ObjectChange;

import java.util.Map;

/**
 * This class checks whether a process involves a name server change or not.
 *
 * @author Patrycja Wegrzynowicz
 */
public class NameServerChange implements DecisionHandler {

    public String decide(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if(td != null) {
            ObjectChange change = td.getDomainChange();
            if (change.getFieldChanges().containsKey("nameServers"))
                return "ns-change";
        }
        return "no-ns-change";
    }
}
