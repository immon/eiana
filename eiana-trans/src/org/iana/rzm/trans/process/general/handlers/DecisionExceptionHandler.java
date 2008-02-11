package org.iana.rzm.trans.process.general.handlers;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.dns.CheckHelper;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

/**
 * This is a superclass for <code>DecisionHandler</code>s,
 * which have to transit the process to the EXCEPTION state,
 * when an exception is thrown.
 *
 * @author Jakub Laszkiewicz
 * @author Piotr Tkaczyk
 */
public abstract class DecisionExceptionHandler implements DecisionHandler {

    protected CheckHelper technicalCheckHelper;

    final public String decide(ExecutionContext executionContext) throws Exception {
        technicalCheckHelper = (CheckHelper) executionContext.getJbpmContext().getObjectFactory().createObject("technicalCheckHelperBean");
        try {
            return doDecide(executionContext);
        } catch (Exception e) {
            TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
            td.setStateMessage(e.getMessage());
            executionContext.getToken().setNode(executionContext.getProcessDefinition().getNode("EXCEPTION"));
        }
        return "TRANSITION_EXCEPTION";
    }

    public abstract String doDecide(ExecutionContext executionContext) throws Exception;
}
