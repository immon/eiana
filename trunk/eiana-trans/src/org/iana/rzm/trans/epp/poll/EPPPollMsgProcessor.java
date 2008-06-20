package org.iana.rzm.trans.epp.poll;

import org.apache.log4j.*;
import org.iana.criteria.*;
import org.iana.epp.exceptions.*;
import org.iana.epp.response.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.epp.*;
import org.iana.rzm.trans.errors.*;
import org.iana.ticketing.*;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
public class EPPPollMsgProcessor implements EPPPollStatusQuery {

    private static Logger logger = Logger.getLogger(EPPPollMsgProcessor.class);

    private TransactionManager transactionManager;

    private PollMsgManager msgManager;

    private ErrorHandler eppErrorHandler;

    private TicketingService ticketingService;

    public EPPPollMsgProcessor(TransactionManager transactionManager, PollMsgManager msgManager, ErrorHandler eppErrorHandler, TicketingService ticketingService) {
        this.ticketingService = ticketingService;
        this.transactionManager = transactionManager;
        this.msgManager = msgManager;
        this.eppErrorHandler = eppErrorHandler;
    }

    public boolean queryAndProcess(EPPPollReq req) throws EPPException, EPPFrameworkException {
        try {
            PollResponse rsp = req.query();
            process(rsp);
            req.ack(rsp);
            return rsp.getMessageCount() > 0;
        } catch (EPPNoMsgException e) {
            return false;
        }
    }

    private void process(PollResponse rsp) {
        String id = rsp.getChangeRequestId();
        long ticketID = new EPPChangeReqId(id).getTicketID();
        Criterion ticketIDCrit = new Equal("ticketID", ticketID);
        List<Transaction> transactions = transactionManager.find(ticketIDCrit);
        if (!transactions.isEmpty()) {
            Transaction trans = transactions.get(0);
            msgManager.create(new PollMsg(trans, rsp.getChangeStatus(), rsp.getMessage()));
            try {
                ticketingService.addComment(trans.getTicketID(), "Poll message: " + rsp.getMessage());
            } catch (TicketingException e) {
                logger.error("Could add poll message comment to RT ", e);
            }
        } else {
            logger.error("no transaction found for ticket id: " + ticketID);
            eppErrorHandler.handleException("no transaction found for ticket id: " + ticketID);
        }
    }
}
