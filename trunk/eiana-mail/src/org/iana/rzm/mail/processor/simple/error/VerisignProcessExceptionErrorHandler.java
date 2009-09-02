package org.iana.rzm.mail.processor.simple.error;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.ticketing.TicketingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class VerisignProcessExceptionErrorHandler extends AbstractErrorHandler {

    public VerisignProcessExceptionErrorHandler(NotificationProducer producer, NotificationSender sender, TicketingService ticketingService) {
        super(producer, sender, ticketingService);
    }


    public void error(String from, String subject, String content, Exception e) {
        sendToAdmins(from, subject, content, e);
    }

    private void sendToAdmins(String from, String subject, String content, Exception exception) {
        try {
            Map<String, Object> dataSource = new HashMap<String, Object>();

            dataSource.put("from", from);
            dataSource.put("subject", subject);
            dataSource.put("content", content);

            String msg = exception.getMessage();
            if (msg == null) msg = "Exception " + exception.getClass() + " occured.";
            dataSource.put("message", msg);
            dataSource.put("exception", exception);

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

