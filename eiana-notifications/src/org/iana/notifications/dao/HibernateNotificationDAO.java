package org.iana.notifications.dao;

import org.iana.notifications.Notification;
import org.iana.notifications.Addressee;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

    public void deleteUserNotifications(Addressee addressee) {
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
                getHibernateTemplate().update(notif);
        }
    }

    public List<Notification> findUserNotifications(Addressee addressee) {
        String query = " select notif " +
                " from " +
                "   Notification as notif "+
                "   inner join notif.addressee as addr "+
                " where "+
                "   addr = ? ";
        return (List<Notification>) getHibernateTemplate().find(query, addressee);
    }

    public List<Notification> findUnSentNotifications(long maxSentFailures) {
        String query = " select notif " +
                " from " +
                "   Notification as notif " +
                "where notif.sent = false and "+
                "notif.sentFailures < ?";
        return (List<Notification>) getHibernateTemplate().find(query, maxSentFailures);
    }
}
