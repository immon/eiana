package org.iana.notifications;

import org.iana.notifications.dao.NotificationDAO;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.criteria.And;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

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

    public List<Notification> findAll() {
        return notificationDAO.findAll();
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

    public List<Notification> findPersistentNotifications(Long transactionId) {
        return notificationDAO.findPersistentNotifications(transactionId);
    }

    public void deletePersistentNotifications(Long transactionId) {
        List<Notification> notifications = findPersistentNotifications(transactionId);
        for(Notification notif : notifications) delete(notif);
    }

    public void deletePersistentNotifications(Long transactionId, String type) {
        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transactionId));
        criteria.add(new Equal(NotificationCriteriaFields.PERSISTENT, true));
        criteria.add(new Equal(NotificationCriteriaFields.TYPE, type));
        List<Notification> notifications = find(new And(criteria));
        for(Notification notif : notifications) delete(notif);
    }

    public List<Notification> find(Criterion criteria) {
        return notificationDAO.find(criteria);
    }
}
