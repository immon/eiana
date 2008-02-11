package org.iana.notifications.refactored;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface NotificationSender {

    void send(PNotification notification) throws NotificationSenderException;

}
