package org.iana.rzm.trans;

import org.iana.notifications.refactored.NotificationSender;
import org.iana.notifications.refactored.PNotification;
import org.iana.notifications.refactored.NotificationSenderException;
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
