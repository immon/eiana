package org.iana.rzm.trans.process.general.logs;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionStateMessageAlert extends ActionExceptionHandler {
    String message;

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        StringBuffer stateMessage = new StringBuffer();
        stateMessage.append("Alert in state: ")
                .append(executionContext.getProcessInstance().getRootToken().getNode().getName())
                .append(":").append(message);
        td.setStateMessage(stateMessage.toString());
    }
}
