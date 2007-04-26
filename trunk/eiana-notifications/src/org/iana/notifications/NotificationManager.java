package org.iana.notifications;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public interface NotificationManager {
    public Notification       get(long id);
    public void               create(Notification notification);
    public void               update(Notification notification);
    public List<Notification> findUserNotifications(Addressee addressee);
    public List<Notification> findUnSentNotifications(long maxSentFailures);
    public void               deleteUserNotifications(Addressee addressee);
}
