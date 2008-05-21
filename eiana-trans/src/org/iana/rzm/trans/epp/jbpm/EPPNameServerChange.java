package org.iana.rzm.trans.epp.jbpm;

import org.iana.epp.EPPClient;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.epp.EPPChangeReq;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class EPPNameServerChange extends ActionExceptionHandler {

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction trans = getTransaction(executionContext);
        HostManager hostManager = (HostManager) executionContext.getJbpmContext().getObjectFactory().createObject("hostManager");
        EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
        EPPChangeReq req = new EPPChangeReq(trans, hostManager, eppClient);
        req.send();
    }

}
