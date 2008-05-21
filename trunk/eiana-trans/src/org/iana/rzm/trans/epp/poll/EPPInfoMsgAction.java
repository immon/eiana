package org.iana.rzm.trans.epp.poll;

import org.apache.log4j.Logger;
import org.iana.criteria.Criterion;
import org.iana.criteria.In;
import org.iana.epp.EPPClient;
import org.iana.epp.EPPOperationFactory;
import org.iana.epp.exceptions.EPPFrameworkException;
import org.iana.epp.request.ChangeInfoRequest;
import org.iana.epp.request.ChangeRef;
import org.iana.epp.response.ChangeInfoResponse;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.errors.ErrorHandler;

import java.rmi.server.UID;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPInfoMsgAction {

    private static Logger logger = Logger.getLogger(EPPInfoMsgAction.class);

    private TransactionManager transactionManager;

    private EPPClient eppClient;

    private ErrorHandler eppErrorHandler;

    public EPPInfoMsgAction(TransactionManager transactionManager, EPPClient eppClient, ErrorHandler eppErrorHandler) {
        this.transactionManager = transactionManager;
        this.eppClient = eppClient;
        this.eppErrorHandler = eppErrorHandler;
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

    private void queryInfoAndProcess(long transactionID) {
        try {
            Transaction trans = transactionManager.getTransaction(transactionID);
            EppChangeStatus response = queryStatus(trans);
            if (response != null) process(trans, response);
        } catch (Exception e) {
            logger.error("quering info and processing", e);
            eppErrorHandler.handleException(e);
        }
    }

    private EppChangeStatus queryStatus(Transaction trans) throws EPPFrameworkException {
        EPPOperationFactory operationFactory = eppClient.getEppOperationFactory();
        ChangeRef ref = operationFactory.getChangeRef(trans.getEppRequestId(), true);
        ChangeInfoRequest req = operationFactory.getChangeInfoRequest(id(), ref);
        ChangeInfoResponse rsp = eppClient.info(req);
        if (rsp == null || !rsp.isSuccessful() || rsp.getStatus() == null) return null;
        return EppChangeStatus.statusOf(rsp.getStatus());
    }

    private void process(Transaction trans, EppChangeStatus status) throws TransactionException {
        if (status == EppChangeStatus.complete) {
            trans.complete();
        } else if (status.getOrderNumber() >= EppChangeStatus.generated.getOrderNumber()) {
            trans.generated();
        } else if (status.getOrderNumber() == -1) {
            trans.exception(status.toString());
        }
    }

    private String id() {
        return new UID().toString();
    }

}
