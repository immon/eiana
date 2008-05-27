package org.iana.rzm.trans.epp.info;

import org.apache.log4j.Logger;
import org.iana.criteria.Criterion;
import org.iana.criteria.In;
import org.iana.epp.EPPClient;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.epp.EPPException;
import org.iana.rzm.trans.epp.SimpleIdGenerator;
import org.iana.rzm.trans.errors.ErrorHandler;
import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPChangeInfoAction implements EPPStatusQuery {

    private static Logger logger = Logger.getLogger(EPPChangeInfoAction.class);

    private TransactionManager transactionManager;

    private EPPClient eppClient;

    private TicketingService ticketingService;

    private ErrorHandler eppErrorHandler;

    public EPPChangeInfoAction(TransactionManager transactionManager, EPPClient eppClient, ErrorHandler eppErrorHandler, TicketingService ticketingService) {
        this.transactionManager = transactionManager;
        this.eppClient = eppClient;
        this.eppErrorHandler = eppErrorHandler;
        this.ticketingService = ticketingService;
    }

    public void execute() throws Exception {
        try {
            List<Transaction> awaiting = transactionManager.find(awaiting());
            for (Transaction trans : awaiting) {
                queryInfoAndProcess(trans.getTransactionID());
            }
        } catch (Exception e) {
            logger.error("while processing awaiting messages", e);
        }
    }

    private Criterion awaiting() {
        Set<String> states = new HashSet<String>();
        states.add(TransactionState.Name.PENDING_ZONE_INSERTION.toString());
        states.add(TransactionState.Name.PENDING_ZONE_PUBLICATION.toString());
        return new In("state", states);
    }

    public void queryInfoAndProcess(long transactionID) {
        try {
            queryStatusAndProcess(transactionID);
        } catch (Exception e) {
            logger.error("quering info and processing", e);
            eppErrorHandler.handleException(e);
        }
    }

    public EPPChangeStatus queryStatusAndProcess(long transactionID) throws EPPException, TransactionException {
        Transaction trans = transactionManager.getTransaction(transactionID);
        EPPChangeStatus response = queryStatus(trans);
        process(trans, response);
        return response;
    }

    private EPPChangeStatus queryStatus(Transaction trans) throws EPPException {
        EPPChangeInfoReq req = new EPPChangeInfoReq(eppClient, new SimpleIdGenerator());
        return req.queryStatus(trans);
    }

    private void process(Transaction trans, EPPChangeStatus status) throws TransactionException {
        try {
            boolean updated = trans.updateEPPStatus(status);
            if (updated) {
                ticketingService.addComment(trans.getTicketID(), "EPP status: " + status);
                if (status == EPPChangeStatus.complete) {
                    trans.complete();
                } else if (status.getOrderNumber() >= EPPChangeStatus.generated.getOrderNumber()) {
                    trans.generated();
                } else if (status.getOrderNumber() == -1) {
                    trans.exception(status.toString());
                }
            }
        } catch (TicketingException e) {
            throw new TransactionException(e);
        }
    }

}
