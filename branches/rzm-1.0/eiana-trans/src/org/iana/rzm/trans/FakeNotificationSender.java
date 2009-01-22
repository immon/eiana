package org.iana.rzm.trans;

import org.iana.dao.*;
import org.iana.notifications.*;

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
