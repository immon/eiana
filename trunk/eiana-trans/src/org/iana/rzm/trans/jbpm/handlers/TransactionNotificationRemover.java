package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.exe.ExecutionContext;
import org.iana.notifications.NotificationManager;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionNotificationRemover extends ActionExceptionHandler {
    String notification;

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        NotificationManager notificationManager = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        if (notification == null)
            notificationManager.deletePersistentNotifications(executionContext.getProcessInstance().getId());
        else
            notificationManager.deletePersistentNotifications(executionContext.getProcessInstance().getId(), notification);
    }
}
