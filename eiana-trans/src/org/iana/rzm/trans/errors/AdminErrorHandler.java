package org.iana.rzm.trans.errors;

import org.iana.notifications.NotificationSender;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * It sends a notification to the email address.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class AdminErrorHandler implements ErrorHandler {

    private static Logger logger = Logger.getLogger(AdminErrorHandler.class);

    private NotificationProducer producer;

    private NotificationSender sender;

    public AdminErrorHandler(NotificationProducer producer, NotificationSender sender) throws IllegalArgumentException {
        CheckTool.checkNull(producer, "notification producer");
        CheckTool.checkNull(sender, "notification sender");
        this.producer = producer;
        this.sender = sender;
    }

    public void handleException(Exception exception) throws IllegalArgumentException {
        CheckTool.checkNull(exception, "exception");
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            StringWriter out = new StringWriter();
            exception.printStackTrace(new PrintWriter(out));
            data.put("exception", out.toString());
            for (PNotification notification : producer.produce(data)) {
                sender.send( notification);
            }
        } catch (Exception e) {
            logger.error("admin error exception handler", e);
        }
    }
}
