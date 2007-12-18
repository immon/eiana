package org.iana.notifications;

import org.iana.criteria.Criterion;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */

public interface NotificationManager {

    public Notification get(long id);

    public void create(Notification notification);

    public void update(Notification notification);

    public void delete(Notification notification);

    public List<Notification> findUserNotifications(Addressee addressee);

    public List<Notification> findUnSentNotifications(long maxSentFailures);

    public List<Notification> findAll();

    public void sendUnSentNotifications(long maxSentFailures);

    public void deleteNotificationsByAddresse(Addressee addressee);

    public List<Notification> findPersistentNotifications(Long transactionId);

    public void deletePersistentNotifications(Long transactionId);

    public void deletePersistentNotifications(Long transactionId, String type);

    public List<Notification> find(Criterion criteria);
}
