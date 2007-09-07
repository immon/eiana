package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.notifications.NotificationManager;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionNotificationRemover extends ActionExceptionHandler {
    protected void doExecute(ExecutionContext executionContext) throws Exception {
        NotificationManager notificationManager = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        notificationManager.deletePersistentNotifications(executionContext.getProcessInstance().getId());
    }
}
