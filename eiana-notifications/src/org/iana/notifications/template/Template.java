package org.iana.notifications.template;

import org.iana.notifications.PContent;
import org.iana.notifications.producers.AddresseeProducer;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Template {

    AddresseeProducer getAddresseeProducer();

    void setAddresseeProducer(AddresseeProducer producer);

    PContent instantiate(Object data) throws TemplateInstantiationException ;

    String getMailSenderType();
}
