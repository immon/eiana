package org.iana.rzm.trans.jbpm.handlers;

import org.iana.notifications.Notification;
import org.iana.notifications.NotificationSender;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.NotificationDataSource;
import org.iana.rzm.trans.notifications.producer.NotificationProducer;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 * @author Jakub Laszkiewicz
 */
public class ProcessStateNotifier extends ActionExceptionHandler {

    private String notificationProducerName = "emptyNotificationProducer";
    private Map<String, Object> additionalData = new HashMap<String, Object>();

    public void doExecute(ExecutionContext executionContext) throws Exception {
        try {
        ObjectFactory objectFactory = executionContext.getJbpmContext().getObjectFactory();
        NotificationSender notificationSender = (NotificationSender) objectFactory.createObject("ticketCommentNotificationSender");
        NotificationProducer notificationProducer = (NotificationProducer) objectFactory.createObject(notificationProducerName);
        Map dataSource = new NotificationDataSource(executionContext, additionalData);
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        if (!td.getCurrentDomain().isEnableEmails()) return;
        for (Notification notification : notificationProducer.produce(dataSource)) {
            if (!notification.getAddressee().isEmpty())
                notificationSender.send(notification);
        }
        }catch(Exception e) {
            int i=0;
        }
    }
}
