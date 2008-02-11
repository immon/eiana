package org.iana.rzm.trans.notifications.jbpm;

import org.iana.notifications.PNotification;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.rzm.trans.notifications.TransactionNotificationSender;
import org.iana.rzm.trans.process.general.ctx.NotificationContext;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
public class ProcessStateNotifier extends ActionExceptionHandler {

    private String notificationProducerName = "emptyNotificationProducer";

    private Map<String, Object> additionalData = new HashMap<String, Object>();

    public void doExecute(ExecutionContext executionContext) throws Exception {
        NotificationContext ctx = new NotificationContext(executionContext);
        TransactionNotificationSender sender = ctx.getSender();
        NotificationProducer producer = ctx.getProducer(notificationProducerName);
        Map data = ctx.getData(additionalData);
        for (PNotification notification : producer.produce(data)) {
            sender.send(ctx.getTransaction(), notification);
        }
    }
}
