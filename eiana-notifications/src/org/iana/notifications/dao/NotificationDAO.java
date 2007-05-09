package org.iana.notifications.dao;

import org.iana.notifications.Notification;
import org.iana.notifications.Addressee;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public interface NotificationDAO {
    public Notification       get(long id);
    public void               create(Notification notification);
    public void               update(Notification notification);
    public void               delete(Notification notification);
    public List<Notification> findUserNotifications(Addressee addressee);
    public List<Notification> findUnSentNotifications(long maxSentFailures);
}
