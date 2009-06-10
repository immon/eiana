package org.iana.rzm.trans.process.general.handlers;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.ctx.TransactionContext;
import org.jbpm.graph.exe.ExecutionContext;

public class RoleCalculator extends ActionExceptionHandler {

    public void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionContext ctx = new TransactionContext(executionContext);
        Transaction trans = ctx.getTransaction();
        // trans.resetConfirmation();
    }
}
