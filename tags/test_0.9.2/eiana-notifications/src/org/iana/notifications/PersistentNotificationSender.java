package org.iana.notifications;

import org.iana.dao.DataAccessObject;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PersistentNotificationSender implements NotificationSender {

    DataAccessObject<PNotification> dao;

    public PersistentNotificationSender(DataAccessObject<PNotification> dao) {
        this.dao = dao;
    }

    public void send(PNotification notification) throws NotificationSenderException {
        dao.create(notification);
    }
}
