package org.iana.rzm.trans.jbpm.handlers.epp;

import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.epp.EPPChangeRequest;
import org.iana.rzm.trans.jbpm.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class EPPNameServerChange extends ActionExceptionHandler {

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction transaction = new Transaction(executionContext.getProcessInstance());
        HostManager hostManager = (HostManager) executionContext.getJbpmContext().getObjectFactory().createObject("hostManager");
        EPPChangeRequest req = new EPPChangeRequest(transaction, hostManager);
        req.send();
    }

}
