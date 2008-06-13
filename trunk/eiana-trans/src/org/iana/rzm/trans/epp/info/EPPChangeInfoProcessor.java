package org.iana.rzm.trans.epp.info;

import org.iana.rzm.trans.epp.EPPException;
import org.iana.rzm.trans.epp.SimpleIdGenerator;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.errors.ErrorHandler;
import org.iana.epp.EPPClient;
import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;
import org.apache.log4j.Logger;

/**
 * @author Piotr Tkaczyk
 */

public class EPPChangeInfoProcessor implements EPPStatusQuery {

    private static Logger logger = Logger.getLogger(EPPChangeInfoAction.class);


    private TransactionManager transactionManager;

    private EPPClient eppClient;

    private TicketingService ticketingService;

    private ErrorHandler eppErrorHandler;


    public EPPChangeInfoProcessor(TransactionManager transactionManager, EPPClient eppClient, TicketingService ticketingService, ErrorHandler eppErrorHandler) {
        this.transactionManager = transactionManager;
        this.eppClient = eppClient;
        this.ticketingService = ticketingService;
        this.eppErrorHandler = eppErrorHandler;
    }

    public void process(long transactionID) {
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
                } else if (status == EPPChangeStatus.docApproved) {
                    trans.usdocAccepted();
                } else if (status == EPPChangeStatus.docRejected) {
                    trans.usdocRejected();
                } else if (status.getOrderNumber() >= EPPChangeStatus.generated.getOrderNumber()) {
                    trans.generated();
                } else if (status.getOrderNumber() == -1) {
                    String msg = "EPP exception status: " + status;
                    trans.exception(msg);
                }
            }
        } catch (TicketingException e) {
            throw new TransactionException(e);
        }
    }


}
