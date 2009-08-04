package org.iana.rzm.trans.process.pending_clarifications;

import org.iana.epp.EPPClient;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.epp.EPPDeleteChange;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public class PendingClarificationsAction extends ActionExceptionHandler {
    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        if (td.isNameServerChange()) {
            Transaction trans = getTransaction(executionContext);
            EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
            EPPDeleteChange del = new EPPDeleteChange(eppClient, trans);
            del.execute();
        }
    }
}
