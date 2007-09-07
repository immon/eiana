package org.iana.notifications.dao;

import org.iana.notifications.Addressee;
import org.iana.notifications.Notification;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public class HibernateNotificationDAO extends HibernateDaoSupport implements NotificationDAO {

    public Notification get(long id) {
        return (Notification) getHibernateTemplate().get(Notification.class, id);
    }

    public void create(Notification notification) {
        getHibernateTemplate().save(notification);
    }

    public void update(Notification notification) {
        getHibernateTemplate().update(notification);
    }

    public void delete(Notification notification) {
        getHibernateTemplate().delete(notification);
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
}
