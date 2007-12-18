package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.NotificationSender;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class NotificationsReSender extends ActionExceptionHandler {

    protected int maxSendNbr;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        NotificationManager notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        NotificationSender notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("ticketCommentNotificationSender");

        List<Notification> unSentNotifications = notificationManagerBean.findUnSentNotifications(maxSendNbr);
        for (Notification notification : unSentNotifications)
            notificationSender.send(notification);
    }
}
