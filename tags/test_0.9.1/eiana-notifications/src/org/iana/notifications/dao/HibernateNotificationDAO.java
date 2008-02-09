package org.iana.notifications.dao;

import org.iana.dao.hibernate.HibernateDAO;
import org.iana.notifications.Addressee;
import org.iana.notifications.Notification;

import java.util.HashSet;
import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public class HibernateNotificationDAO extends HibernateDAO<Notification> implements NotificationDAO {

    public HibernateNotificationDAO() {
        super(Notification.class);
    }

    public List<Notification> findUserNotifications(Addressee addressee) {
        String query = " select notif " +
                " from " +
                "   Notification as notif " +
                "   inner join notif.addressee as addr " +
                " where " +
                "   addr = ? ";
        return (List<Notification>) getHibernateTemplate().find(query, addressee);
    }

    public List<Notification> findUnSentNotifications(long maxSentFailures) {
        String query = " select notif " +
                " from " +
                "   Notification as notif " +
                "where notif.sent = false and " +
                "notif.sentFailures < ?";
        return (List<Notification>) getHibernateTemplate().find(query, maxSentFailures);
    }

    public List<Notification> findAll() {
        return getHibernateTemplate().find("from Notification");
    }

    public List<Notification> findPersistentNotifications(Long transactionId) {
        String query = " select notif " +
                "from " +
                "    Notification as notif " +
                "where notif.persistent = true " +
                "    and transactionId = ?";
        return (List<Notification>) getHibernateTemplate().find(query, transactionId);
    }

    /*
     Removing associations with RZMUser objects,
     because they cause hibernate error when removing
     persistent notifications in the end of the process.
     */
    public void delete(Notification object) {
        object.setAddressee(new HashSet<Addressee>());
        super.delete(object);
    }
}
