package org.iana.notifications;

import org.iana.notifications.exception.NotificationException;
import java.util.Collection;

/**
 * It sends a notification to a given addressee. The notification is sent via email as well as is stored for further use.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface NotificationSender {

    void send(Addressee addressee, String subject, String body) throws NotificationException;

    void send(Collection<Addressee> addressees, String subject, String body) throws NotificationException;

    void send(Addressee addressee, Content content) throws NotificationException;

    void send(Collection<Addressee> addressees, Content content) throws NotificationException;

    void send(Notification notification) throws NotificationException;
}
