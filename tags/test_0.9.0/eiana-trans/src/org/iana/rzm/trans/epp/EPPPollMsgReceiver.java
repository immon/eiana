package org.iana.rzm.trans.epp;

import org.iana.epp.EPPClient;
import org.iana.rzm.trans.TransactionManager;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPPollMsgReceiver implements ActionHandler {
    public void execute(ExecutionContext executionContext) throws Exception {
        TransactionManager transactionManager = (TransactionManager) executionContext.getJbpmContext().getObjectFactory().createObject("transactionManager");
        EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
        EPPPollRequest eppPollRequest = new EPPPollRequest(eppClient);
        EppChangeRequestPollRsp rsp = eppPollRequest.send();
        rsp.accept(transactionManager);
    }
}
