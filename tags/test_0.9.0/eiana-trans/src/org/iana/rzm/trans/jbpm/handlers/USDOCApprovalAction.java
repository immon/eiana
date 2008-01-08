package org.iana.rzm.trans.jbpm.handlers;

import org.iana.epp.EPPClient;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.DomainChangePrinter;
import org.iana.rzm.trans.epp.EPPChangeRequest;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Jakub Laszkiewicz
 */
public class USDOCApprovalAction extends ActionExceptionHandler {
    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        td.setupUSDoCConfirmation();
        if (td.isNameServerChange()) {
            HostManager hostManager = (HostManager) executionContext.getJbpmContext().getObjectFactory().createObject("hostManager");
            Transaction trans = new Transaction(executionContext.getProcessInstance());
            EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
            EPPChangeRequest eppChangeRequest = new EPPChangeRequest(trans, hostManager, eppClient);
            String[] rsp = eppChangeRequest.send();
            executionContext.getContextInstance().setVariable("receipt", rsp[1]);
            executionContext.getContextInstance().setVariable("eppID", rsp[0]);
        }
    }

}
