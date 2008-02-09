package org.iana.rzm.trans.jbpm.handlers.epp;

import org.iana.epp.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.epp.*;
import org.iana.rzm.trans.jbpm.handlers.*;
import org.jbpm.graph.exe.*;

public class EPPNameServerChange extends ActionExceptionHandler {

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction transaction = new Transaction(executionContext.getProcessInstance());
        HostManager hostManager = (HostManager) executionContext.getJbpmContext().getObjectFactory().createObject("hostManager");
        EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
        EPPChangeRequest req = new EPPChangeRequest(transaction, hostManager, eppClient);
        req.send();
    }

}
