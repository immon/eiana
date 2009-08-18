package org.iana.rzm.mail.processor.simple.error;

import org.apache.log4j.Logger;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.NotificationSenderException;
import org.iana.notifications.PAddressee;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.defaults.DefaultAddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SimpleEmailErrorHandler implements EmailErrorHandler {

    private static Logger logger = Logger.getLogger(SimpleEmailErrorHandler.class);

    private static final String RESPONSE_PREFIX = "Re: ";

    public static final String GENERAL_EMAIL_EXCEPTION_NOTIFICATION_PRODUCER = "generalEmailExceptionNotificationProducer";

    private NotificationSender notifier;
    private Map<String, NotificationProducer> notificationProducers;

    public SimpleEmailErrorHandler(NotificationSender notifier, Map<String, NotificationProducer> notificationProducers) {
        CheckTool.checkNull(notifier, "notification sender");
        this.notifier = notifier;
        this.notificationProducers = notificationProducers;
    }

    public void error(String to, String originalSubject, String originalContent, String message) {
        try {
            String subject = RESPONSE_PREFIX + originalSubject;
            String quotedContent = originalContent != null ? quote(originalContent) + "\n" : "";
            String content = quotedContent + message;
            PNotification notification = new PNotification(PNotification.UNKNOWN, PNotification.DEFAULT_MAIL_SENDER,
                    new PAddressee(to, to), subject, content);
            notifier.send(notification);
        } catch (NotificationSenderException e) {
            logger.error("Unexpected notifier exception", e);
        }
    }

    private String quote(String s) {
        StringTokenizer st = new StringTokenizer(s, "\n");
        StringBuilder result = new StringBuilder();
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            line = quoteLine(line);
            result.append(line).append("\n");
        }
        return result.toString();
    }

    private String quoteLine(String line) {
        if (line == null) return null;
        return "> " + line;
    }

    public void error(String to, String subject, String content, Exception exception) {
        String msg = exception.getMessage();
        if (msg == null) msg = "Exception " + exception.getClass() + " occured.";
        error(to, subject, content, msg);
    }

    public void error(String to, String subject, String content, Exception exception, String notificationProducer) {
        try {
            String nProducer = notificationProducer;

            if(nProducer == null || nProducer.trim().length() == 0) nProducer = GENERAL_EMAIL_EXCEPTION_NOTIFICATION_PRODUCER;

            NotificationProducer producer = notificationProducers.get(nProducer);

            if (producer == null) {
                logger.error("There is no defined notification producer for: " + nProducer);
                error(to, subject, content, exception);
            } else {
                Map<String, Object> dataSource = new HashMap<String, Object>();
                dataSource.put(DefaultAddresseeProducer.ADDRESSEE, new PAddressee(to, to));

                dataSource.put("subject", subject);
                dataSource.put("content", content);

                String msg = exception.getMessage();
                if (msg == null) msg = "Exception " + exception.getClass() + " occured.";
                dataSource.put("message", msg);

                List<PNotification> pNotifications = producer.produce(dataSource);
                for (PNotification notification : pNotifications) {
                    notifier.send(notification);
                }
            }
        } catch (NotificationProducerException e) {
            logger.error("Unexpected notification producer exception", e);
        } catch (NotificationSenderException e) {
            logger.error("Unexpected notifier exception", e);
        }
    }
}
