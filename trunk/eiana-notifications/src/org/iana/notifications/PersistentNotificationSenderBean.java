package org.iana.notifications;

import org.iana.notifications.exception.NotificationException;

import java.util.Collection;

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
        try {
            notificationSender.send(notification.getAddressee(), notification.getContent());
            if (notification.isPersistent())
                notificationManager.create(notification);
        } catch (NotificationException e) {
            notification.incSentFailures();
            notificationManager.create(notification);
        }
    }

    public void send(Collection<Addressee> addressees, String subject, String body) throws NotificationException {
        notificationSender.send(addressees, subject, body);
    }

    public void send(Addressee addressee, Content content) throws NotificationException {
        notificationSender.send(addressee, content);
    }

    public void send(Collection<Addressee> addressees, Content content) throws NotificationException {
        notificationSender.send(addressees, content);    
    }
}
