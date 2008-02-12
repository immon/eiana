package org.iana.rzm.trans.process.general.handlers;

import org.apache.log4j.Logger;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.dns.CheckHelper;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This is a superclass for <code>ActionHandler</code>s,
 * which have to transit the process to the EXCEPTION state,
 * when an exception is thrown.
 *
 * @author Jakub Laszkiewicz
 * @author Piotr Tkaczyk
 */
public abstract class ActionExceptionHandler implements ActionHandler {
    protected CheckHelper technicalCheckHelper;

    final public void execute(ExecutionContext executionContext) throws Exception {

        technicalCheckHelper = (CheckHelper) executionContext.getJbpmContext().getObjectFactory().createObject("technicalCheckHelperBean");
        try {
            doExecute(executionContext);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(getClass()).error("in jbpm action", e);
            TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
            td.setStateMessage(e.getMessage());
            executionContext.getToken().setNode(executionContext.getProcessDefinition().getNode("EXCEPTION"));
        }
    }

    protected abstract void doExecute(ExecutionContext executionContext) throws Exception;
}
