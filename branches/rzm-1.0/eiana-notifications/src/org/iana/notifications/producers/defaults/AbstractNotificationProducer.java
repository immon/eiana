package org.iana.notifications.producers.defaults;

import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.producers.DataProducer;
import org.iana.notifications.producers.NotificationProducer;
import org.iana.notifications.producers.TemplateNameProducer;
import org.iana.notifications.template.factory.TemplateFactory;
import org.iana.rzm.common.validators.CheckTool;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public abstract class AbstractNotificationProducer implements NotificationProducer {

    protected TemplateFactory templateFactory;

    protected TemplateNameProducer templateNameProducer;

    protected AddresseeProducer addresseeProducer;

    protected DataProducer dataProducer;

    protected boolean persistent = false;

    public AbstractNotificationProducer(TemplateFactory contentFactory, List<String> addressees, String templateName, DataProducer dataProducer) {
        this(contentFactory, new ListAddresseeProducer(addressees), new DefaultTemplateNameProducer(templateName), dataProducer);
    }

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
