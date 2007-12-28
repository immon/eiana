package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.ContentFactory;
import org.iana.notifications.Notification;
import org.iana.rzm.trans.TransactionData;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class TransactionPersistentNotificationProducer extends SingleNotificationProducer {

    public TransactionPersistentNotificationProducer(ContentFactory contentFactory, AddresseeProducer addresseeProducer, TemplateProducer templateProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateProducer, dataProducer);
    }

    protected Notification createNotification(Map<String, Object> dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        Notification notification = new Notification((Long) dataSource.get("transactionId"), td.getTicketID());
        notification.setPersistent(true);
        return notification;
    }
}
