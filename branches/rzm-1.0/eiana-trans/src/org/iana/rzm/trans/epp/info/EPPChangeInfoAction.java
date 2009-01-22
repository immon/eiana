package org.iana.rzm.trans.epp.info;

import org.apache.log4j.Logger;
import org.iana.criteria.Criterion;
import org.iana.criteria.In;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.TransactionException;
import org.iana.rzm.trans.epp.EPPExecutor;
import org.iana.rzm.trans.epp.EPPException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jakub Laszkiewicz
 */
public class EPPChangeInfoAction implements EPPExecutor, EPPStatusQuery {

    private static Logger logger = Logger.getLogger(EPPChangeInfoAction.class);

    private TransactionManager transactionManager;

    private EPPStatusQuery eppChangeInfoProcessor;

    public EPPChangeInfoAction(TransactionManager transactionManager, EPPStatusQuery eppChangeInfoProcessor) {
        this.transactionManager = transactionManager;
        this.eppChangeInfoProcessor = eppChangeInfoProcessor;
    }

    public void execute() {
        try {
            List<Transaction> awaiting = transactionManager.find(awaiting());
            for (Transaction trans : awaiting) {
                eppChangeInfoProcessor.process(trans.getTransactionID());
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


    public void process(long transactionID) {
        eppChangeInfoProcessor.process(transactionID);
    }

    public EPPChangeStatus queryStatusAndProcess(long transactionID) throws EPPException, TransactionException {
        return eppChangeInfoProcessor.queryStatusAndProcess(transactionID);
    }
}
