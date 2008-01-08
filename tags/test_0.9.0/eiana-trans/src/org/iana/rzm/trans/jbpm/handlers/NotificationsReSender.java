package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.NotificationManager;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author: Piotr Tkaczyk
 */
public class NotificationsReSender extends ActionExceptionHandler {

    protected int maxSendNbr;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        NotificationManager notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        notificationManagerBean.sendUnSentNotifications(maxSendNbr);
    }
}
