package org.iana.rzm.trans.jbpm.handlers;

import org.iana.epp.EPPClient;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.epp.EPPPollRequest;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class ZonePublicationAction extends ActionExceptionHandler {
    public static final String POLL_MESSAGE_USDOC_APPROVED = "The DoC has approved this Change Request.";

    public void doExecute(ExecutionContext executionContext) throws java.lang.Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
        EPPPollRequest eppPollRequest = new EPPPollRequest(trans.getTicketID(), eppClient);
        String rsp = eppPollRequest.send();
        if (POLL_MESSAGE_USDOC_APPROVED.equals(rsp)) executionContext.leaveNode("accept");
    }
}
