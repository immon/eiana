package org.iana.rzm.trans.epp;

import org.apache.log4j.Logger;
import org.iana.epp.EPPClient;
import org.iana.rzm.trans.errors.ErrorHandler;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPPollMsgAction {

    private static Logger logger = Logger.getLogger(EPPPollMsgAction.class);

    private EppPollMsgProcessor eppPollMsgProcessor;

    private EPPClient eppClient;

    private ErrorHandler eppErrorHandler;

    public EPPPollMsgAction(EppPollMsgProcessor eppPollMsgProcessor, EPPClient eppClient, ErrorHandler eppErrorHandler) {
        this.eppPollMsgProcessor = eppPollMsgProcessor;
        this.eppClient = eppClient;
        this.eppErrorHandler = eppErrorHandler;
    }

    public void execute() throws Exception {
        try {
            EppChangeRequestPollRsp rsp;
            do {
                EPPPollRequest eppPollRequest = new EPPPollRequest(eppClient);
                rsp = eppPollRequest.send();
                try {
                    rsp.accept(eppPollMsgProcessor);
                } catch (Exception e) {
                    logger.error("while processing message", e);
                    eppErrorHandler.handleException(e);
                }
            } while (rsp.isMessageAwaiting());
        } catch (Exception e) {
            logger.error("while processing awaiting messages", e);
        }
    }
}
