package org.iana.rzm.trans.notifications.producer;

import org.iana.notifications.ContentFactory;
import org.iana.notifications.Notification;
import org.iana.rzm.common.validators.CheckTool;

import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
abstract class AbstractNotificationProducer implements NotificationProducer {

    protected ContentFactory contentFactory;
    protected TemplateProducer templateProducer;
    protected AddresseeProducer addresseeProducer;
    protected DataProducer dataProducer;

    public AbstractNotificationProducer(ContentFactory contentFactory, AddresseeProducer addresseeProducer, TemplateProducer templateProducer, DataProducer dataProducer) {
        CheckTool.checkNull(contentFactory, "content factory is null");
        CheckTool.checkNull(addresseeProducer, "addressee producer is null");
        CheckTool.checkNull(templateProducer, "template producer is null");
        CheckTool.checkNull(dataProducer, "data producer is null");
        this.contentFactory = contentFactory;
        this.addresseeProducer = addresseeProducer;
        this.templateProducer = templateProducer;
        this.dataProducer = dataProducer;
    }

    abstract public List<Notification> produce(Map<String, Object> dataSource);

    abstract protected Notification createNotification(Map<String, Object> dataSource);
}
