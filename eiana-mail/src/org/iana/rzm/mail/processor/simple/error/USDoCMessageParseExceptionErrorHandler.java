package org.iana.rzm.mail.processor.simple.error;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PAddressee;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.defaults.DefaultAddresseeProducer;
import org.iana.rzm.mail.processor.usdoc.USDoCMessageParseException;
import org.iana.ticketing.TicketingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCMessageParseExceptionErrorHandler extends AbstractErrorHandler {

    public USDoCMessageParseExceptionErrorHandler(NotificationProducer producer, NotificationSender sender, TicketingService ticketingService) {
        super(producer, sender, ticketingService);
    }

    public void error(String from, String subject, String content, Exception e) {
        USDoCMessageParseException exception = (USDoCMessageParseException) e;

        logToRT(exception.getTicketId(), e.getMessage());

        sendBackToUSDoC(from, subject, content, exception);
    }

    private void sendBackToUSDoC(String from, String subject, String content, USDoCMessageParseException exception) {
        try {
            Map<String, Object> dataSource = new HashMap<String, Object>();

            dataSource.put(DefaultAddresseeProducer.ADDRESSEE, new PAddressee(from, from));

            dataSource.put("subject", subject);
            dataSource.put("content", content);

            dataSource.put("ticket", exception.getTicketId());

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
