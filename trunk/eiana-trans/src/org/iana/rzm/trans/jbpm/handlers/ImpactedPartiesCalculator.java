package org.iana.rzm.trans.jbpm.handlers;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesCalculator implements ActionHandler {

    public void execute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        Token token = executionContext.getProcessInstance().getRootToken();
        Node node = token.getNode();
        td.setStateConfirmations(node.getName(), new StateConfirmations());
    }
}
