package org.iana.notifications.refactored.producers.defaults;

import org.iana.notifications.refactored.producers.AddresseeProducer;
import org.iana.notifications.refactored.producers.DataProducer;
import org.iana.notifications.refactored.producers.NotificationProducer;
import org.iana.notifications.refactored.producers.TemplateNameProducer;
import org.iana.notifications.refactored.template.factory.TemplateFactory;
import org.iana.rzm.common.validators.CheckTool;

/**
 * @author Piotr Tkaczyk
 */
abstract class AbstractNotificationProducer implements NotificationProducer {

    protected TemplateFactory templateFactory;

    protected TemplateNameProducer templateNameProducer;

    protected AddresseeProducer addresseeProducer;

    protected DataProducer dataProducer;

    protected boolean persistent = false;

    public AbstractNotificationProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, String templateName, DataProducer dataProducer) {
        this(contentFactory, addresseeProducer, new DefaultTemplateNameProducer(templateName), dataProducer);
    }

    public AbstractNotificationProducer(TemplateFactory contentFactory, AddresseeProducer addresseeProducer, TemplateNameProducer templateNameProducer, DataProducer dataProducer) {
        CheckTool.checkNull(contentFactory, "content factory is null");
        CheckTool.checkNull(addresseeProducer, "addressee producer is null");
        CheckTool.checkNull(templateNameProducer, "template producer is null");
        CheckTool.checkNull(dataProducer, "data producer is null");
        this.templateFactory = contentFactory;
        this.addresseeProducer = addresseeProducer;
        this.templateNameProducer = templateNameProducer;
        this.dataProducer = dataProducer;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
}
