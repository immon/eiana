package org.iana.rzm.trans.epp;

import org.iana.rzm.trans.*;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class EppPollMsgProcessorImpl implements EppPollMsgProcessor {
    private TransactionManager transactionManager;
    private Logger logger = Logger.getLogger(EppPollMsgProcessorImpl.class);

    public EppPollMsgProcessorImpl(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void visitDocApproved(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        logger.info("Received poll message status: DocApproved for transaction id: " +
                trans.getTransactionID());
    }

    public void visitDocApprovalTimeout(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        logger.info("Received poll message status: DocApprovalTimeout for transaction id: " +
                trans.getTransactionID());
    }

    public void visitDocRejected(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        logger.info("Received poll message status: DocRejected for transaction id: " +
                trans.getTransactionID());
    }

    public void visitSystemValidated(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        logger.info("Received poll message status: SystemValidated for transaction id: " +
                trans.getTransactionID());
    }

    public void visitValidationError(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        logger.info("Received poll message status: ValidationError for transaction id: " +
                trans.getTransactionID());
    }

    public void visitHold(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        logger.info("Received poll message status: Hold for transaction id: " +
                trans.getTransactionID());
    }

    public void visitGenerated(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        if (trans.getState().getName() != TransactionState.Name.PENDING_ZONE_INSERTION)
            throw new IllegalTransactionStateException(trans.getState());
        trans.systemAccept();
    }

    public void visitNsRejected(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        logger.info("Received poll message status: NsRejected for transaction id: " +
                trans.getTransactionID());
    }

    public void visitComplete(String eppRequestId) throws TransactionException {
        Transaction trans = findByChangeRequestID(eppRequestId);
        if (trans.getState().getName() != TransactionState.Name.PENDING_ZONE_PUBLICATION)
            throw new IllegalTransactionStateException(trans.getState());
        trans.systemAccept();
    }

    Transaction findByChangeRequestID(String changeRequestID) throws TransactionException {
        Criterion eppRequestIdCriterion = new Equal("eppRequestId", changeRequestID);
        List<Transaction> found = transactionManager.find(eppRequestIdCriterion);
        if (found == null || found.isEmpty())
            throw new NoSuchTransactionException(changeRequestID);
        if (found.size() > 1)
            throw new TransactionException(changeRequestID + " non unique");
        return found.iterator().next();
    }
}
