package org.iana.rzm.trans.notifications;

import org.iana.notifications.*;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.trans.NoSuchTransactionException;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.TransactionManager;
import org.iana.ticketing.TicketingException;
import org.iana.ticketing.TicketingService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TicketCommentNotificationSender implements NotificationSender {

    private NotificationSender notificationSender;

    private TicketingService ticketingService;

    private TransactionManager transactionManager;

    public TicketCommentNotificationSender(NotificationSender notificationSender, TicketingService ticketingService, TransactionManager transactionManager) {
        this.notificationSender = notificationSender;
        this.ticketingService = ticketingService;
        this.transactionManager = transactionManager;
    }

    public void send(Addressee addressee, String subject, String body) throws NotificationException {
        send(addressee, new TextContent(subject, body));
    }

    public void send(Notification notification) throws NotificationException {
        notificationSender.send(notification);
        if (notification.getTransactionId() != null) {
            try {
                Transaction transaction = transactionManager.getTransaction(notification.getTransactionId());
                Long ticketID = transaction.getTicketID();
                if (ticketID != null) {
                    Notification2Comment conv = new Notification2Comment(notification);
                    ticketingService.addComment(ticketID, conv.getComment());
                }
            } catch (NoSuchTransactionException e) {
                throw new NotificationException(e);
            } catch (TicketingException e) {
                throw new NotificationException(e);
            }
        }
    }


    public void send(Collection<Addressee> addressees, String subject, String body) throws NotificationException {
        Notification notification = new Notification();
        notification.setAddressee(new HashSet<Addressee>(addressees));
        notification.setContent(new TextContent(subject, body));
        send(notification);
    }

    public void send(Addressee addressee, Content content) throws NotificationException {
        Notification notification = new Notification();
        notification.setAddressee(new HashSet<Addressee>(Arrays.asList(addressee)));
        notification.setContent(content);
        send(notification);
    }

    public void send(Collection<Addressee> addressees, Content content) throws NotificationException {
        Notification notification = new Notification();
        notification.setAddressee(new HashSet<Addressee>(addressees));
        notification.setContent(content);
        send(notification);
    }

}
