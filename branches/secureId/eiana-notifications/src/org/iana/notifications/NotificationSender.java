package org.iana.notifications;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface NotificationSender {

    void send(PNotification notification) throws NotificationSenderException;

}
