package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.Addressee;
import org.iana.notifications.Content;
import org.iana.notifications.ContentFactory;
import org.iana.notifications.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class SplittedNotificationProducer extends AbstractNotificationProducer {

    public SplittedNotificationProducer(ContentFactory contentFactory, AddresseeProducer addresseeProducer, TemplateProducer templateProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateProducer, dataProducer);
    }

    public List<Notification> produce(Map<String, Object> dataSource) {
        List<Notification> notifications = new ArrayList<Notification>();
        for (Addressee addreessee : addresseeProducer.produce(dataSource)) {
            for (String template : templateProducer.produce(dataSource)) {
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
        return new Notification();
    }
}
