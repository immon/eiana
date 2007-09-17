package org.iana.notifications.dao;

import org.iana.criteria.Criterion;
import org.iana.notifications.Addressee;
import org.iana.notifications.Notification;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public interface NotificationDAO {

    public Notification get(long id);

    public void create(Notification notification);

    public void update(Notification notification);

    public void delete(Notification notification);

    public List<Notification> findUserNotifications(Addressee addressee);

    public List<Notification> findUnSentNotifications(long maxSentFailures);

    public List<Notification> findAll();

    public List<Notification> findPersistentNotifications(Long transactionId);

    public List<Notification> find(Criterion criteria);

    public List<Notification> find(final Criterion criteria, final int offset, final int limit);

    public int count(final Criterion criteria);
}
