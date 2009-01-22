/*
package org.iana.rzm.trans.notifications.producer.old;

import org.iana.notifications.PAddressee;
import org.iana.notifications.PContent;
import org.iana.notifications.ContentFactory;
import org.iana.notifications.PNotification;
import org.iana.notifications.refactored.PNotification;
import org.iana.notifications.refactored.PContent;
import org.iana.notifications.refactored.PAddressee;
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
public class SplittedNotificationProducer extends AbstractNotificationProducer {

    public SplittedNotificationProducer(ContentFactory contentFactory, AddresseeProducer addresseeProducer, TemplateNameProducer templateNameProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateNameProducer, dataProducer);
    }

    public List<PNotification> produce(Map<String, Object> dataSource) {
        List<PNotification> notifications = new ArrayList<PNotification>();
        for (PAddressee addreessee : addresseeProducer.produce(dataSource)) {
            for (String template : templateNameProducer.produce(dataSource)) {
                Map<String, String> values = dataProducer.getValuesMap(dataSource);
                PContent templateContent = templateFactory.createContent(template, values);
                PNotification notification = createNotification(dataSource);
                notification.addAddressee(addreessee);
                notification.setContent(templateContent);
                notifications.add(notification);
            }
        }
        return notifications;
    }

    protected PNotification createNotification(Map<String, Object> dataSource) {
        return new PNotification();
    }
}
*/
