package org.iana.notifications.producers.defaults;

import org.iana.notifications.PAddressee;
import org.iana.notifications.PContent;
import org.iana.notifications.PNotification;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.producers.DataProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.TemplateNameProducer;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateInstantiationException;
import org.iana.notifications.template.TemplateNotFoundException;
import org.iana.notifications.template.factory.TemplateFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class SinglePerTemplateProducer extends AbstractNotificationProducer {

    public SinglePerTemplateProducer(TemplateFactory contentFactory, List<String> addressees, String templateName, DataProducer dataProducer) {
        super(contentFactory, addressees, templateName, dataProducer);
    }

    public SinglePerTemplateProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, String templateName, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateName, dataProducer);
    }

    public SinglePerTemplateProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, TemplateNameProducer templateNameProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateNameProducer, dataProducer);
    }

    public List<PNotification> produce(Map<String, Object> dataSource) throws NotificationProducerException {
        try {
            List<PNotification> notifications = new ArrayList<PNotification>();
            for (String templateName : templateNameProducer.produce(dataSource)) {
                Template template = templateFactory.getTemplate(templateName);
                AddresseeProducer addrProducer = template.getAddresseeProducer();
                if (addrProducer == null) addrProducer = addresseeProducer;
                Set<PAddressee> addressees = addrProducer.produce(dataSource);
                Map<String, String> values = dataProducer.getValuesMap(dataSource);
                PContent content = template.instantiate(values);
                PNotification notification = new PNotification(
                        templateName, addressees, content, persistent
                );
                notifications.add(notification);
            }
            return notifications;
        } catch (TemplateNotFoundException e) {
            throw new NotificationProducerException(e);
        } catch (TemplateInstantiationException e) {
            throw new NotificationProducerException(e);
        }
    }
}
