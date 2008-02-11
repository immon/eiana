package org.iana.rzm.trans;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.PNotification;
import org.iana.notifications.NotificationSenderException;
import org.iana.dao.DataAccessObject;

/**
 * @author Patrycja Wegrzynowicz
 */
public class FakeNotificationSender implements NotificationSender {

    DataAccessObject<PNotification> dao;

    public FakeNotificationSender(DataAccessObject<PNotification> dao) {
        this.dao = dao;
    }

    public void send(PNotification notification) throws NotificationSenderException {
        dao.create(notification);
    }
}
