package org.iana.notifications.producers.defaults;

import org.iana.notifications.PAddressee;
import org.iana.notifications.PNotification;
import org.iana.notifications.PContent;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.producers.DataProducer;
import org.iana.notifications.producers.NotificationProducerException;
import org.iana.notifications.producers.TemplateNameProducer;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateInstantiationException;
import org.iana.notifications.template.factory.TemplateFactory;

import java.util.*;

/**
 * @author PiotrTkaczyk
 */
public class SplittedByAddresseeProducer extends AbstractNotificationProducer {

    public SplittedByAddresseeProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, String templateName, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateName, dataProducer);
    }

    public SplittedByAddresseeProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, TemplateNameProducer templateNameProducer, DataProducer dataProducer) {
        super(contentFactory, addresseeProducer, templateNameProducer, dataProducer);
    }

    public List<PNotification> produce(Map<String, Object> dataSource) throws NotificationProducerException {
        try {
            List<PNotification> notifications = new ArrayList<PNotification>();
            for (PAddressee addreessee : addresseeProducer.produce(dataSource)) {
                for (String templateName : templateNameProducer.produce(dataSource)) {
                    // inject addressee into the data source map...
                    dataSource.put("addressee", addreessee);
                    // ...and pass it to the data producer
                    Map<String, String> values = dataProducer.getValuesMap(dataSource);
                    Template template = templateFactory.getTemplate(templateName);
                    PContent content = template.instantiate(values);
                    Set<PAddressee> addressees = new HashSet<PAddressee>();
                    addressees.add(addreessee);
                    PNotification notification = new PNotification(
                            templateName, addressees, content, persistent
                    );
                    notifications.add(notification);
                }
            }
            return notifications;
        } catch (TemplateInstantiationException e) {
            throw new NotificationProducerException(e);
        }
    }

}
