package org.iana.notifications.template.factory;

import org.apache.log4j.Logger;
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

    private Set<String> recipients;

    public ConfiguredRecipients(Map<String, AddresseeProducer> producers, Set<String> recipients) {
        CheckTool.checkNull(producers, "addressee producers");
        CheckTool.checkNull(recipients, "recipients");
        this.producers = producers;
        this.recipients = recipients;
    }

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        for (String recipient : recipients) {
            Set<PAddressee> addrs = getAddresses(recipient, dataSource);
            ret.addAll(addrs);
        }
        return ret;
    }

    protected Set<PAddressee> getAddresses(String recipient, Map dataSource) {
        AddresseeProducer ap = producers.get(recipient);
        if (ap != null) {
            return ap.produce(dataSource);
        } else {
            Set<PAddressee> ret = new HashSet<PAddressee>();
            if (CheckTool.isCorrectEmali(recipient)) {
                ret.add(new PAddressee(recipient, recipient));
            } else {
                Logger.getLogger(getClass()).warn(recipient + " is not a valid email address");
            }
            return ret;
        }
    }
    
}
