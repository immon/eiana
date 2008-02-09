package org.iana.rzm.trans.epp;

import org.apache.log4j.Logger;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.rzm.trans.*;

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

    public void visitDocApproved(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: DocApproved for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
    }

    public void visitDocApprovalTimeout(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: DocApprovalTimeout for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
    }

    public void visitDocRejected(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: DocRejected for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
    }

    public void visitSystemValidated(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: SystemValidated for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
    }

    public void visitValidationError(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: ValidationError for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
    }

    public void visitHold(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: Hold for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
    }

    public void visitGenerated(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: visitGenerated for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
        if (trans.getState().getName() != TransactionState.Name.PENDING_ZONE_INSERTION)
            throw new IllegalTransactionStateException(trans.getState());
        trans.systemAccept();
    }

    public void visitNsRejected(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: NsRejected for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
    }

    public void visitComplete(EppChangeRequestPollRsp pollRsp) throws TransactionException {
        Transaction trans = findByChangeRequestID(pollRsp.getChangeRequestId());
        logger.info("Received poll message status: visitComplete for transaction id: " +
                trans.getTransactionID() + ":\n" + pollRsp.getMessage() + "\n" + pollRsp.getErrors());
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
