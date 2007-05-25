package org.iana.rzm.system.accuracy;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.Content;
import org.iana.notifications.Addressee;
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
}
