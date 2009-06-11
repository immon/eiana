package org.iana.notifications;

import org.apache.log4j.Logger;
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

        if(notification.getAddressees().size() == 0){
            Logger.getLogger(getClass()).error("Trying to send notification to an empty address list " +
                    notification.getType() + " " + notification.getContent().getBody());
            return;
        }
        
        sender.send(notification);
        notification.markAsSent();
        if (notification.isPersistent()) {
            dao.create(notification);
        }
    }
    
}

