package org.iana.rzm.trans.notifications;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PNotification;
import org.iana.rzm.trans.Transaction;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionNotificationSender {

    NotificationSender sender;

    TicketingService ticketSystem;

    public TransactionNotificationSender(NotificationSender sender, TicketingService ticketSystem) {
        this.sender = sender;
        this.ticketSystem = ticketSystem;
    }

    public void send(Transaction trans, PNotification notification) throws NotificationSenderException {
        if (trans.areEmailsEnabled()) {
            sender.send(notification);
            trans.addNotification(notification);
            Long ticketID = trans.getTicketID();
            if (ticketID != null) {
                try {
                    Notification2Comment conv = new Notification2Comment(notification);
                    ticketSystem.addComment(ticketID, conv.getComment());
                } catch (TicketingException e) {
                    throw new NotificationSenderException(e);
                }
            }
        } else {
            // todo: log info
        }
    }
    
}