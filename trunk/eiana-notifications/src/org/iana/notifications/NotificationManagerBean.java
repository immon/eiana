package org.iana.notifications;

import org.iana.notifications.dao.NotificationDAO;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

    public List<Notification> findUnSentNotifications(long maxSentFailures) {
        return notificationDAO.findUnSentNotifications(maxSentFailures);
    }
    
    public void deleteNotificationsByAddresse(Addressee addressee) {
        List<Notification> notifications = findUserNotifications(addressee);
        for(Notification notif : notifications) {
            Set<Addressee> newAddressee = new HashSet<Addressee>();
            for(Addressee addr : notif.getAddressee()) {
                if (!addr.getObjId().equals(addressee.getObjId()))
                    newAddressee.add(addr);
            }
            notif.setAddressee(newAddressee);
            if (newAddressee.isEmpty())
                delete(notif);
            else
                update(notif);
        }
    }
}
