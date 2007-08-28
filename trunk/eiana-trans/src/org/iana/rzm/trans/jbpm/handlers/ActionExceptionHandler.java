package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * This is a superclass for <code>ActionHandler</code>s,
 * which have to transit the process to the EXCEPTION state,
 * when an exception is thrown.
 *
 * @author Jakub Laszkiewicz
 */
public abstract class ActionExceptionHandler implements ActionHandler {
    final public void execute(ExecutionContext executionContext) throws Exception {
        try {
            doExecute(executionContext);
        } catch (Exception e) {
            executionContext.getToken().setNode(executionContext.getProcessDefinition().getNode("EXCEPTION"));
        }
    }

    protected abstract void doExecute(ExecutionContext executionContext) throws Exception;
}
