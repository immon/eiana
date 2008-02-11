package org.iana.rzm.trans.process.general.ctx;

import org.iana.notifications.refactored.producers.NotificationProducer;
import org.iana.rzm.trans.notifications.NotificationDataSource;
import org.iana.rzm.trans.notifications.TransactionNotificationSender;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NotificationContext extends TransactionContext {

    public NotificationContext(ExecutionContext ctx) {
        super(ctx);
    }

    public NotificationProducer getProducer(String name) {
        return (NotificationProducer) getObjectFactory().createObject(name);
    }

    public TransactionNotificationSender getSender() {
        return (TransactionNotificationSender) getObjectFactory().createObject("transactionNotificationSender");
    }

    public Map getData(Map additionalData) {
        return new NotificationDataSource(ctx, additionalData);
    }
}
