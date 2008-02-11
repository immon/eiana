package org.iana.rzm.trans.process.general.domainstate;

import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ThirdPartyPending extends ActionExceptionHandler {

    public boolean start;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        DomainManager manager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
        manager.updateThirdPartyPendingProcesses(trans.getCurrentDomain().getName(), start);
    }
}
