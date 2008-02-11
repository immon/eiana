package org.iana.notifications.refactored;

import org.iana.dao.DataAccessObject;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PNotificationStore implements NotificationSender {

    private NotificationSender sender;

    private DataAccessObject<PNotification> dao;

    public PNotificationStore(NotificationSender sender, DataAccessObject<PNotification> dao) {
        CheckTool.checkNull(sender, "sender");
        CheckTool.checkNull(dao, "dao");
        this.sender = sender;
        this.dao = dao;
    }

    public void send(PNotification notification) throws NotificationSenderException {
        sender.send(notification);
        notification.markAsSent();
        if (notification.isPersistent()) {
            dao.create(notification);
        }
    }
    
}

