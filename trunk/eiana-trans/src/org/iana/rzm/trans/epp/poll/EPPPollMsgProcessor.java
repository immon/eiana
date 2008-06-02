package org.iana.rzm.trans.epp.poll;

import org.apache.log4j.Logger;
import org.iana.epp.response.PollResponse;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.epp.EPPException;
import org.iana.rzm.trans.epp.EPPChangeReqId;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class EPPPollMsgProcessor implements EPPPollStatusQuery {

    private static Logger logger = Logger.getLogger(EPPPollMsgAction.class);

    private TransactionManager transactionManager;

    private PollMsgManager msgManager;

    public EPPPollMsgProcessor(TransactionManager transactionManager, PollMsgManager msgManager) {
        this.transactionManager = transactionManager;
        this.msgManager = msgManager;
    }

    public boolean queryAndProcess(EPPPollReq req) throws EPPException, EPPFrameworkException {

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
