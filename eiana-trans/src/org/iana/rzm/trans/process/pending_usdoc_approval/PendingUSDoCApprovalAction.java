package org.iana.rzm.trans.process.pending_usdoc_approval;

import org.iana.epp.EPPClient;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.epp.EPPChangeReq;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Jakub Laszkiewicz
 */
public class PendingUSDoCApprovalAction extends ActionExceptionHandler {
    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        td.setupUSDoCConfirmation();
        if (td.isNameServerChange()) {
            HostManager hostManager = (HostManager) executionContext.getJbpmContext().getObjectFactory().createObject("hostManager");
            Transaction trans = getTransaction(executionContext);
            EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
            EPPChangeReq eppChangeReq = new EPPChangeReq(trans, hostManager, eppClient);
            String[] rsp = eppChangeReq.send();
            td.setEppReceipt(rsp[1]);
        }
    }

}
