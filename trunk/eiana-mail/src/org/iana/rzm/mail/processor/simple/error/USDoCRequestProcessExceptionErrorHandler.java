package org.iana.rzm.mail.processor.simple.error;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PAddressee;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.defaults.DefaultAddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.usdoc.USDoCRequestProcessException;
import org.iana.ticketing.TicketingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCRequestProcessExceptionErrorHandler extends AbstractErrorHandler {

    NotificationProducer adminProducer;

    public USDoCRequestProcessExceptionErrorHandler(NotificationProducer adminProducer, NotificationProducer producer, NotificationSender sender, TicketingService ticketingService) {
        super(producer, sender, ticketingService);
        CheckTool.checkNull(adminProducer, "to admin notification producer");
        this.adminProducer = adminProducer;
    }

    public void error(String from, String subject, String content, Exception e) {
        USDoCRequestProcessException exception = (USDoCRequestProcessException) e;

        if (exception.getTransaction() == null)
            sendToIANAAdmins(from, subject, content, exception);

        logToRT(exception.getTransaction(), exception.getMessage());
        sendBackToUSDoC(from, subject, content, exception);

    }

    private void sendToIANAAdmins(String from, String subject, String content, USDoCRequestProcessException exception) {
        try {
            Map<String, Object> dataSource = new HashMap<String, Object>();

            dataSource.put("from", from);
            dataSource.put("subject", subject);
            dataSource.put("content", content);

            String msg = exception.getMessage();
            if (msg == null) msg = "Exception " + exception.getClass() + " occured.";
            dataSource.put("message", msg);
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

    private void sendBackToUSDoC(String from, String subject, String content, USDoCRequestProcessException exception) {
        try {
            Map<String, Object> dataSource = new HashMap<String, Object>();
            dataSource.put(DefaultAddresseeProducer.ADDRESSEE, new PAddressee(from, from));

            dataSource.put("subject", subject);
            dataSource.put("content", content);

            dataSource.put("ticket", exception.getTransaction().getTicketID());

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
