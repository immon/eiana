package org.iana.notifications;

import org.iana.criteria.And;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.notifications.dao.NotificationDAO;
import org.iana.notifications.exception.NotificationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class NotificationManagerBean implements NotificationManager {
    private NotificationDAO notificationDAO;
    private ContentConverter templateContentConverter;
    private NotificationSender notificationSender;

    public NotificationManagerBean(NotificationDAO notificationDAO, ContentConverter contentConverter, NotificationSender notificationSender) {
        this.notificationDAO = notificationDAO;
        this.templateContentConverter = contentConverter;
        this.notificationSender = notificationSender;
    }

    public Notification get(long id) {
        Notification notification = notificationDAO.get(id);
        addContentConverter(notification);
        return notification;
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
        List<Notification> ret = notificationDAO.findUserNotifications(addressee);
        for (Notification notification : ret)
            addContentConverter(notification);
        return ret;
    }

    public List<Notification> findUnSentNotifications(long maxSentFailures) {
        List<Notification> ret = notificationDAO.findUnSentNotifications(maxSentFailures);
        for (Notification notification : ret)
            addContentConverter(notification);
        return ret;
    }


    public void sendUnSentNotifications(long maxSentFailures) {
        for (Notification notification : findUnSentNotifications(maxSentFailures)) {
            try {
                notificationSender.send(notification);
                notification.setSent(true);
                update(notification);
                if (!notification.isPersistent()) delete(notification);
            } catch (NotificationException e) {
                notification.incSentFailures();
                update(notification);
            }
        }
    }

    public List<Notification> findAll() {
        List<Notification> ret = notificationDAO.findAll();
        for (Notification notification : ret)
            addContentConverter(notification);
        return ret;
    }

    public void deleteNotificationsByAddresse(Addressee addressee) {
        List<Notification> notifications = findUserNotifications(addressee);
        for (Notification notif : notifications) {
            Set<Addressee> newAddressee = new HashSet<Addressee>();
            for (Addressee addr : notif.getAddressee()) {
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
        List<Notification> ret = notificationDAO.findPersistentNotifications(transactionId);
        for (Notification notification : ret)
            addContentConverter(notification);
        return ret;
    }

    public void deletePersistentNotifications(Long transactionId) {
        List<Notification> notifications = findPersistentNotifications(transactionId);
        for (Notification notif : notifications) delete(notif);
    }

    public void deletePersistentNotifications(Long transactionId, String type) {
        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(new Equal(NotificationCriteriaFields.TRANSACTION_ID, transactionId));
        criteria.add(new Equal(NotificationCriteriaFields.PERSISTENT, true));
        criteria.add(new Equal(NotificationCriteriaFields.TYPE, type));
        List<Notification> notifications = find(new And(criteria));
        for (Notification notif : notifications) delete(notif);
    }

    public List<Notification> find(Criterion criteria) {
        List<Notification> ret = notificationDAO.find(criteria);
        for (Notification notification : ret)
            addContentConverter(notification);
        return ret;
    }

    private void addContentConverter(Notification notification) {
        Content content = notification.getContent();
        if (content != null && content.isTemplateContent()) {
            ((TemplateContent) content).setTemplateConverter(templateContentConverter);
            notification.setContent(content);
        }
    }
}
