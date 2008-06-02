
package org.iana.rzm.trans.epp.poll;

import org.apache.log4j.Logger;
import org.iana.epp.EPPClient;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.epp.SimpleIdGenerator;
import org.iana.rzm.trans.epp.EPPExecutor;


/**
 * @author Patrycja Wegrzynowicz
 */
public class EPPPollMsgAction implements EPPExecutor {

    private static Logger logger = Logger.getLogger(EPPPollMsgAction.class);

    private EPPClient eppClient;

    private EPPPollStatusQuery eppPollMsgProcessor;

    public EPPPollMsgAction(EPPClient eppClient, EPPPollStatusQuery eppPollMsgProcessor) {
        CheckTool.checkNull(eppClient, "epp client");
        CheckTool.checkNull(eppPollMsgProcessor, "epp poll msg processor");
        this.eppClient = eppClient;
        this.eppPollMsgProcessor = eppPollMsgProcessor;
    }

    public void execute() {
        try {
            EPPPollReq req = new EPPPollReq(eppClient, new SimpleIdGenerator());
            while (eppPollMsgProcessor.queryAndProcess(req));
        } catch (Exception e) {
            logger.error("while getting epp poll messages", e);
        }
    }
}
