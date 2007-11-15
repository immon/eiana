package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.NotificationManager;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class TransactionNotificationRemover extends ActionExceptionHandler {
    List<String> notifications;

    protected void doExecute(ExecutionContext executionContext) throws Exception {
        NotificationManager notificationManager = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        if (notifications == null || notifications.isEmpty())
            notificationManager.deletePersistentNotifications(executionContext.getProcessInstance().getId());
        else {
            for (String notif : notifications)
                notificationManager.deletePersistentNotifications(executionContext.getProcessInstance().getId(), notif);
        }
    }
}
