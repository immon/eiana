package org.iana.rzm.trans.jbpm.handlers.domainstate;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.iana.notifications.NotificationManager;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.Transaction;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ProcessLifecycle implements ActionHandler {

    public boolean start;

    public void execute(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        DomainManager manager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
        manager.updateOpenProcesses(trans.getCurrentDomain().getName(), start);
    }
}
