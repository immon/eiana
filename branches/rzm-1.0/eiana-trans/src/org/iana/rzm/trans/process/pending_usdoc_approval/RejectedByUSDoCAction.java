package org.iana.rzm.trans.process.pending_usdoc_approval;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class RejectedByUSDoCAction  extends ActionExceptionHandler {

    private String rejectionMsg = "rejected by USDoC";

    public void setRejectionMsg(String rejectionMsg) {
        this.rejectionMsg = rejectionMsg;
    }

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        td.setStateMessage(rejectionMsg);
    }

}
