package org.iana.notifications;

import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;

/**
 * @author Jakub Laszkiewicz
 */
public class PersistentNotificationSenderBean implements NotificationSender {
    private NotificationSender notificationSender;
    private NotificationManager notificationManager;

    public PersistentNotificationSenderBean(NotificationSender notificationSender, NotificationManager notificationManager) {
        this.notificationSender = notificationSender;
        this.notificationManager = notificationManager;
    }

    public void send(Addressee addressee, String subject, String body) throws NotificationException {
        notificationSender.send(addressee, subject, body);
    }

    public void send(Notification notification) throws NotificationException {
        CheckTool.checkNull(notification, null);                
        boolean persist = notification.isPersistent();
        try {
            notificationSender.send(notification.getAddressee(), notification.getContent());
        } catch (NotificationException e) {
            notification.incSentFailures();
            persist = true;
        }
        if (persist) notificationManager.create(notification);
    }

    public void send(Collection<Addressee> addressees, String subject, String body) throws NotificationException {
        Notification notification = new Notification();
        notification.setAddressee(new HashSet<Addressee>(addressees));
        notification.setContent(new TextContent(subject, body));
        send(notification);
    }

    public void send(Addressee addressee, Content content) throws NotificationException {
        Notification notification = new Notification();
        notification.setAddressee(new HashSet<Addressee>(Arrays.asList(addressee)));
        notification.setContent(content);
        send(notification);
    }

    public void send(Collection<Addressee> addressees, Content content) throws NotificationException {
        Notification notification = new Notification();
        notification.setAddressee(new HashSet<Addressee>(addressees));
        notification.setContent(content);
        send(notification);
    }
}
