package org.iana.rzm.trans.notifications.review;

import org.iana.rzm.domain.Domain;
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
        Domain d = trans.getCurrentDomain();
        if (d.isSpecialReview())
            super.doExecute(executionContext);
    }
}
