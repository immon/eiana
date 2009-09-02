package org.iana.rzm.mail.processor.simple.error;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PAddressee;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.defaults.DefaultAddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.system.trans.vo.TransactionVO;
import org.iana.rzm.mail.processor.contact.ContactRequestProcessException;
import org.iana.ticketing.TicketingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ContactRequestProcessExceptionErrorHandler extends AbstractErrorHandler {

    NotificationProducer adminProducer;

    public ContactRequestProcessExceptionErrorHandler(NotificationProducer adminProducer, NotificationProducer producer, NotificationSender sender, TicketingService ticketingService) {
        super(producer, sender, ticketingService);
        CheckTool.checkNull(adminProducer, "to admins notification producer");
        this.adminProducer = adminProducer;
    }

    public void error(String from, String subject, String content, Exception e) {
        ContactRequestProcessException ex = (ContactRequestProcessException) e;

        TransactionVO transaction = ex.getTransaction();
        logToRT(transaction, e.getMessage());

        if (transaction != null)
            sendBackToSender(from, subject, content, ex);
        else
            sendToAdmins(from, subject, content, ex);
    }

    private void sendBackToSender(String from, String subject, String content, ContactRequestProcessException exception) {
        try {
            Map<String, Object> dataSource = new HashMap<String, Object>();
            dataSource.put(DefaultAddresseeProducer.ADDRESSEE, new PAddressee(from, from));

            dataSource.put("subject", subject);
            dataSource.put("content", content);

            dataSource.put("ticket", exception.getTransaction().getTicketID());

            List<PNotification> pNotifications = producer.produce(dataSource);
            for (PNotification notification : pNotifications) {
                sender.send(notification);
            }
        } catch (NotificationProducerException e) {
            logger.error("Unexpected notification producer exception", e);
        } catch (NotificationSenderException e) {
            logger.error("Unexpected notifier exception", e);
        }
    }

    private void sendToAdmins(String from, String subject, String content, ContactRequestProcessException exception) {
        try {
            Map<String, Object> dataSource = new HashMap<String, Object>();

            dataSource.put("from", from);
            dataSource.put("subject", subject);
            dataSource.put("content", content);

            dataSource.put("exception", exception);

            List<PNotification> pNotifications = adminProducer.produce(dataSource);
            for (PNotification notification : pNotifications) {
                sender.send(notification);
            }
        } catch (NotificationProducerException e) {
            logger.error("Unexpected notification producer exception", e);
        } catch (NotificationSenderException e) {
            logger.error("Unexpected notifier exception", e);
        }
    }
}
