package org.iana.rzm.trans.process.general.handlers;

import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.ctx.TransactionContext;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionNotificationRemover extends ActionExceptionHandler {

    List<String> notifications = new ArrayList<String>();

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionContext ctx = new TransactionContext(executionContext);
        Transaction trans = ctx.getTransaction();
        if (notifications.isEmpty()) {
            trans.deleteAllNotifications();
        } else {
            trans.deleteNotifications(notifications);
        }
    }
}
