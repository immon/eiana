package org.iana.rzm.mail.processor.simple.error;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PAddressee;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.defaults.DefaultAddresseeProducer;
import org.iana.rzm.mail.processor.contact.ContactMessageParseException;
import org.iana.ticketing.TicketingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ContactMessageParseExceptionErrorHandler extends AbstractErrorHandler {

    public ContactMessageParseExceptionErrorHandler(NotificationProducer producer, NotificationSender sender, TicketingService ticketingService) {
        super(producer, sender, ticketingService);
    }

    public void error(String from, String subject, String content, Exception e) {
        ContactMessageParseException ex = (ContactMessageParseException) e;
        logToRT(ex.getTicketId(), e.getMessage());

        sendBackToSender(from, subject, content, ex);
    }

    private void sendBackToSender(String from, String subject, String content, ContactMessageParseException exception) {
        try {
            Map<String, Object> dataSource = new HashMap<String, Object>();
            dataSource.put(DefaultAddresseeProducer.ADDRESSEE, new PAddressee(from, from));

            dataSource.put("subject", subject);
            dataSource.put("content", content);

            String msg = exception.getMessage();
            if (msg == null) msg = "Exception " + exception.getClass() + " occured.";
            dataSource.put("message", msg);

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
}
