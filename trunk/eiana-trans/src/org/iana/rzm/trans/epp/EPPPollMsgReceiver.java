package org.iana.rzm.trans.epp;

import org.apache.log4j.Logger;
import org.iana.epp.EPPClient;
import org.iana.rzm.trans.errors.ErrorHandler;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.configuration.ObjectFactory;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPPollMsgReceiver implements ActionHandler {
    private Logger logger = Logger.getLogger(EPPPollMsgReceiver.class);

    public void execute(ExecutionContext executionContext) throws Exception {
        try {
            ObjectFactory objectFactory = executionContext.getJbpmContext().getObjectFactory();
            EppPollMsgProcessor eppPollMsgProcessor = (EppPollMsgProcessor) objectFactory.createObject("eppPollMsgProcessorBean");
            EPPClient eppClient = (EPPClient) objectFactory.createObject("eppClient");
            ErrorHandler errorHandler = (ErrorHandler) objectFactory.createObject("eppErrorHandler");
            EppChangeRequestPollRsp rsp;
            do {
                EPPPollRequest eppPollRequest = new EPPPollRequest(eppClient);
                rsp = eppPollRequest.send();
                try {
                    rsp.accept(eppPollMsgProcessor);
                } catch (Exception e) {
                    logger.error("while processing message", e);
                    errorHandler.handleException(e);
                }
            } while (rsp.isMessageAwaiting());
        } catch (Exception e) {
            logger.error("while processing awaiting messages", e);
        }
    }
}
