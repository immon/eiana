package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.exception.NotificationException;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.List;

/**
 * @author: Piotr Tkaczyk
 */
public class NotificationsReSender extends ActionExceptionHandler {

    protected int maxSendNbr;

    public void doExecute(ExecutionContext executionContext) throws Exception {
        NotificationManager notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        NotificationSender notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");

        List<Notification> unSentNotifications = notificationManagerBean.findUnSentNotifications(maxSendNbr);
        for (Notification notification : unSentNotifications)
            try {
                notificationSender.send(notification.getAddressee(), notification.getContent());
                notificationManagerBean.delete(notification);
            } catch (NotificationException e) {
                notification.incSentFailures();
                notificationManagerBean.update(notification);
            }
    }
}
