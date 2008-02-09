package org.iana.rzm.facade.passwd;

import org.iana.notifications.Addressee;
import org.iana.notifications.Content;
import org.iana.notifications.Notification;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.exception.NotificationException;

import java.util.Collection;

/**
 * @author Jakub Laszkiewicz
 */
public class FakeNotificationSenderBean implements NotificationSender {
    public void send(Addressee addressee, String subject, String body) throws NotificationException {
    }

    public void send(Collection<Addressee> addressees, String subject, String body) throws NotificationException {
    }

    public void send(Addressee addressee, Content content) throws NotificationException {
    }

    public void send(Collection<Addressee> addressees, Content content) throws NotificationException {
    }

    public void send(Notification notification) throws NotificationException {
    }
}
