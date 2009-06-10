package org.iana.rzm.trans.notifications.review;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.notifications.jbpm.ProcessStateNotifier;
import org.iana.rzm.trans.process.general.ctx.TransactionContext;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public class SpecialReviewProcessStateNotifier extends ProcessStateNotifier {


    public void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionContext ctx = new TransactionContext(executionContext);
        Transaction trans = ctx.getTransaction();
        if (trans.requiresSpecialReview()) {
            super.doExecute(executionContext);
        }
    }
}
