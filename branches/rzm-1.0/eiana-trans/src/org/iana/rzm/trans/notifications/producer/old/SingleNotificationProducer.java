/*
package org.iana.rzm.trans.notifications.producer.old;

import org.iana.notifications.ContentFactory;
import org.iana.notifications.refactored.PNotification;
import org.iana.notifications.refactored.PContent;
import org.iana.rzm.trans.notifications.producer.AddresseeProducer;
import org.iana.rzm.trans.notifications.producer.TemplateNameProducer;
import org.iana.rzm.trans.notifications.producer.DataProducer;
import org.iana.rzm.trans.notifications.producer.AbstractNotificationProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

*/
/**
 * @author Piotr Tkaczyk
 */
/*
public class SingleNotificationProducer extends AbstractNotificationProducer {

    public SingleNotificationProducer(ContentFactory contentFactory, AddresseeProducer addresseeProducer, TemplateNameProducer templateNameProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateNameProducer, dataProducer);
    }

    public List<PNotification> produce(Map<String, Object> dataSource) {
        List<PNotification> notifications = new ArrayList<PNotification>();

        for (String template : templateNameProducer.produce(dataSource)) {
            Map<String, String> notificationData = dataProducer.getValuesMap(dataSource);
            PContent templateContent = templateFactory.createContent(template, notificationData);
            PNotification notification = createNotification(dataSource);
            notification.setAddressees(addresseeProducer.produce(dataSource));
            notification.setContent(templateContent);

            notifications.add(notification);
        }
        return notifications;
    }

    protected PNotification createNotification(Map<String, Object> dataSource) {
        return new PNotification();
    }
}
*/
