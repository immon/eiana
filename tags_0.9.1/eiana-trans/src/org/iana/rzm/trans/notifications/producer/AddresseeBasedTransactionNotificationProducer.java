package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.Notification;
import org.iana.notifications.Addressee;
import org.iana.notifications.Content;
import org.iana.notifications.ContentFactory;
import org.iana.rzm.trans.TransactionData;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class AddresseeBasedTransactionNotificationProducer extends AbstractNotificationProducer {

    public AddresseeBasedTransactionNotificationProducer(ContentFactory contentFactory, AddresseeProducer addresseeProducer, TemplateProducer templateProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateProducer, dataProducer);
    }

    public List<Notification> produce(Map<String, Object> dataSource) {
        List<Notification> notifications = new ArrayList<Notification>();
        for (Addressee addreessee : addresseeProducer.produce(dataSource)) {
            for (String template : templateProducer.produce(dataSource)) {
                // inject addressee into the data source map...
                dataSource.put("addressee", addreessee);
                // ...and pass it to the data producer                
                Map<String, String> values = dataProducer.getValuesMap(dataSource);
                Content templateContent = contentFactory.createContent(template, values);
                Notification notification = createNotification(dataSource);
                notification.addAddressee(addreessee);
                notification.setContent(templateContent);
                notifications.add(notification);
            }
        }
        return notifications;
    }

    protected Notification createNotification(Map<String, Object> dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        Notification notification = new Notification((Long) dataSource.get("transactionId"), td.getTicketID());
        notification.setPersistent(true);
        return notification;
    }

}
