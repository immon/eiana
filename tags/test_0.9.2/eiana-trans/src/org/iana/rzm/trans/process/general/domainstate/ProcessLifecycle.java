package org.iana.rzm.trans.process.general.domainstate;

import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ProcessLifecycle extends ActionExceptionHandler {

    public boolean start;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction trans = getTransaction(executionContext);
        DomainManager manager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
        manager.updateOpenProcesses(trans.getCurrentDomain().getName(), start);
    }
}
