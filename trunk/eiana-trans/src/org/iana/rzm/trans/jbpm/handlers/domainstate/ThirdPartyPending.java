package org.iana.rzm.trans.jbpm.handlers.domainstate;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.def.ActionHandler;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.domain.DomainManager;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ThirdPartyPending implements ActionHandler {

    public boolean start;

    public void execute(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        DomainManager manager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
        manager.updateThirdPartyPendingProcesses(trans.getCurrentDomain().getName(), start);        
    }
}
