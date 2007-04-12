package org.iana.notifications;

import org.iana.notifications.dao.NotificationDAO;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public class NotificationManagerBean implements NotificationManager {
    private NotificationDAO notificationDAO;


    public NotificationManagerBean(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public Notification get(long id) {
        return notificationDAO.get(id);
    }

    public void create(Notification notification) {
        notificationDAO.create(notification);    
    }

    public void update(Notification notification) {
        notificationDAO.update(notification);
    }

    public void delete(Notification notification) {
        notificationDAO.delete(notification);
    }

    public List<Notification> findUserNotifications(Addressee addressee) {
        return notificationDAO.findUserNotifications(addressee);
    }
}
