package org.iana.rzm.trans.epp;

import org.apache.log4j.Logger;
import org.iana.epp.EPPClient;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPPollMsgReceiver implements ActionHandler {
    private Logger logger = Logger.getLogger(EPPPollMsgReceiver.class);

    public void execute(ExecutionContext executionContext) throws Exception {
        try {
            EppPollMsgProcessor eppPollMsgProcessor = (EppPollMsgProcessor)
                    executionContext.getJbpmContext().getObjectFactory().createObject("eppPollMsgProcessorBean");
            EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
            EppChangeRequestPollRsp rsp;
            do {
                EPPPollRequest eppPollRequest = new EPPPollRequest(eppClient);
                rsp = eppPollRequest.send();
                try {
                    rsp.accept(eppPollMsgProcessor);
                } catch (Exception e) {
                    logger.error("while processing message", e);
                }
            } while (rsp.isMessageAwaiting());
        } catch (Exception e) {
            logger.error("while processing awaiting messages", e);
        }
    }
}
