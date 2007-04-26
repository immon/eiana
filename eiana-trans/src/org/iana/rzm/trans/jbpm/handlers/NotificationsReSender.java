package org.iana.rzm.trans.jbpm.handlers;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.notifications.NotificationManager;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.Notification;
import org.iana.notifications.exception.NotificationException;

import java.util.List;

/**
 * @author: piotrt
 */
public class NotificationsReSender implements ActionHandler {

    private NotificationManager notificationManagerBean;
    private NotificationSender  notificationSender;
    private long                notifiyTryNbr;

    public void execute(ExecutionContext executionContext) throws Exception {
        notificationManagerBean = (NotificationManager) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationManagerBean");
        notificationSender = (NotificationSender) executionContext.getJbpmContext().getObjectFactory().createObject("NotificationSenderBean");

        List<Notification> unSentNotifications = notificationManagerBean.findUnSentNotifications(notifiyTryNbr);
        for (Notification notification : unSentNotifications)
            try {
                notificationSender.send(notification.getAddressee(), notification.getContent());
            } catch(NotificationException e) {
                notification.incSentFailures();
                notificationManagerBean .update(notification);
            }
    }
}
