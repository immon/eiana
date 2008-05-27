
package org.iana.rzm.trans.epp.poll;

import org.apache.log4j.Logger;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.epp.EPPClient;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.response.PollResponse;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.epp.EPPChangeReqId;
import org.iana.rzm.trans.epp.EPPException;
import org.iana.rzm.trans.epp.SimpleIdGenerator;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EPPPollMsgAction {

    private static Logger logger = Logger.getLogger(EPPPollMsgAction.class);

    private EPPClient eppClient;

    private TransactionManager transactionManager;

    private PollMsgManager msgManager;

    public EPPPollMsgAction(EPPClient eppClient, TransactionManager transactionManager, PollMsgManager msgDAO) {
        CheckTool.checkNull(eppClient, "epp client");
        CheckTool.checkNull(transactionManager, "transaction manager");
        CheckTool.checkNull(transactionManager, "msg dao");
        this.eppClient = eppClient;
        this.transactionManager = transactionManager;
        this.msgManager = msgDAO;
    }

    public void execute() {
        try {
            EPPPollReq req = new EPPPollReq(eppClient, new SimpleIdGenerator());
            while (queryAndProcess(req));
        } catch (Exception e) {
            logger.error("while getting epp poll messages", e);
        }
    }

    private boolean queryAndProcess(EPPPollReq req) throws EPPException, EPPFrameworkException {
        PollResponse rsp = req.query();
        process(rsp);
        req.ack(rsp);
        return rsp.getMessageCount() > 0;
    }

    private void process(PollResponse rsp) {
        String id = rsp.getChangeRequestId();
        long ticketID = new EPPChangeReqId(id).getTicketID();
        Criterion ticketIDCrit = new Equal("ticketID", ticketID);
        List<Transaction> transactions = transactionManager.find(ticketIDCrit);
        if (!transactions.isEmpty()) {
            Transaction trans = transactions.get(0);
            msgManager.create(new PollMsg(trans, rsp.getChangeStatus(), rsp.getMessage()));
        } else {
            logger.warn("no transaction found for ticket id: " + ticketID);
        }
    }

}
