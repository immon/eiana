package org.iana.notifications.refactored.producers.defaults;

import org.iana.notifications.refactored.PAddressee;
import org.iana.notifications.refactored.PContent;
import org.iana.notifications.refactored.PNotification;
import org.iana.notifications.refactored.producers.AddresseeProducer;
import org.iana.notifications.refactored.producers.DataProducer;
import org.iana.notifications.refactored.producers.NotificationProducerException;
import org.iana.notifications.refactored.producers.TemplateNameProducer;
import org.iana.notifications.refactored.template.Template;
import org.iana.notifications.refactored.template.TemplateInstantiationException;
import org.iana.notifications.refactored.template.factory.TemplateFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class SinglePerTemplateProducer extends AbstractNotificationProducer {

    public SinglePerTemplateProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, String templateName, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateName, dataProducer);
    }

    public SinglePerTemplateProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, TemplateNameProducer templateNameProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateNameProducer, dataProducer);
    }

    public List<PNotification> produce(Map<String, Object> dataSource) throws NotificationProducerException {
        try {
            List<PNotification> notifications = new ArrayList<PNotification>();
            Set<PAddressee> addressees = addresseeProducer.produce(dataSource);
            for (String templateName : templateNameProducer.produce(dataSource)) {
                Map<String, String> values = dataProducer.getValuesMap(dataSource);
                Template template = templateFactory.getTemplate(templateName);
                PContent content = template.instantiate(values);
                PNotification notification = new PNotification(
                        templateName, addressees, content, persistent
                );
                notifications.add(notification);
            }
            return notifications;
        } catch (TemplateInstantiationException e) {
            throw new NotificationProducerException(e);
        }
    }
}
