package org.iana.rzm.trans.jbpm.handlers;

import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.trans.TransactionData;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This class checks whether a process involves a name server change or not.
 *
 * @author Patrycja Wegrzynowicz
 */
public class NameServerChange extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if (td != null) {
            ObjectChange change = td.getDomainChange();
            if ((change != null) && (change.getFieldChanges().containsKey("nameServers")))
                return "ns-change";
        }
        return "no-ns-change";
    }
}
