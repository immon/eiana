/*
package org.iana.rzm.trans.notifications.producer.old;

import org.iana.notifications.ContentFactory;
import org.iana.notifications.refactored.PNotification;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.producer.AddresseeProducer;
import org.iana.rzm.trans.notifications.producer.TemplateNameProducer;
import org.iana.rzm.trans.notifications.producer.DataProducer;

import java.util.Map;

*/
/**
 * @author Piotr Tkaczyk
 */
/*
public class TransactionNotificationProducer extends SingleNotificationProducer {

    public TransactionNotificationProducer(ContentFactory contentFactory, AddresseeProducer addresseeProducer, TemplateNameProducer templateNameProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateNameProducer, dataProducer);
    }

    protected PNotification createNotification(Map<String, Object> dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        return new PNotification((Long) dataSource.get("transactionId"), td.getTicketID());
    }
}
*/
