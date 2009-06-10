package org.iana.rzm.trans.process.general.logs;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionStateMessageCleaner extends ActionExceptionHandler {
    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        td.setStateMessage(null);
    }
}
