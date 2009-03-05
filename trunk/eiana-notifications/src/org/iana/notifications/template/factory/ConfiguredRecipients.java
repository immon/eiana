package org.iana.notifications.template.factory;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ConfiguredRecipients implements AddresseeProducer {

    private Map<String, AddresseeProducer> producers;

    private AddresseeProducer defaultProducer;

    private Set<String> recipients;

    public ConfiguredRecipients(Map<String, AddresseeProducer> producers, AddresseeProducer defaultProducer, Set<String> recipients) {
        CheckTool.checkNull(producers, "addressee producers");
        CheckTool.checkNull(defaultProducer, "default producer");
        CheckTool.checkNull(recipients, "recipients");
        this.producers = producers;
        this.defaultProducer = defaultProducer;
        this.recipients = recipients;
    }

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        for (String recipient : recipients) {
            AddresseeProducer producer = getProducer(recipient);
            ret.addAll(producer.produce(dataSource));
        }
        return ret;
    }

    protected AddresseeProducer getProducer(String recipient) {
        AddresseeProducer ret = producers.get(recipient);
        return ret == null ? defaultProducer : ret;
    }
    
}
