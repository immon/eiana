package org.iana.rzm.trans.jbpm.handlers;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesCalculator extends ActionExceptionHandler {

    public void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        Token token = executionContext.getProcessInstance().getRootToken();
        Node node = token.getNode();
        StateConfirmations sc = (StateConfirmations) td.getStateConfirmations(node.getName());
        if (sc == null) td.setStateConfirmations(node.getName(), new StateConfirmations());
    }
}