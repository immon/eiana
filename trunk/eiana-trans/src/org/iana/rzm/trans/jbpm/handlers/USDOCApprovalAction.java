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
        USDOCConfirmationNotifier notifier = new USDOCConfirmationNotifier();
        if (isNsChange(executionContext)) {
            HostManager hostManager = (HostManager) executionContext.getJbpmContext().getObjectFactory().createObject("hostManager");
            Transaction trans = new Transaction(executionContext.getProcessInstance());
            EPPClient eppClient = (EPPClient) executionContext.getJbpmContext().getObjectFactory().createObject("eppClient");
            EPPChangeRequest eppChangeRequest = new EPPChangeRequest(trans, hostManager, eppClient);
            String[] rsp = eppChangeRequest.send();
            notifier.setReceipt(rsp[1]);
            notifier.setEppID(rsp[0]);
            notifier.setNotification("usdoc-confirmation-nschange");
        } else {
            TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
            notifier.setReceipt(DomainChangePrinter.print(td.getDomainChange()));
            notifier.setNotification("usdoc-confirmation");
        }
        notifier.execute(executionContext);
    }

    private boolean isNsChange(ExecutionContext executionContext) {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        return td != null && td.getDomainChange() != null &&
                td.getDomainChange().getFieldChanges().containsKey("nameServers");
    }
}
