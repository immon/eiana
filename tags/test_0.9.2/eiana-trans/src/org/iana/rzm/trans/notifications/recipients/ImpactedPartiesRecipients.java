package org.iana.rzm.trans.notifications.recipients;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesRecipients extends ContactRecipients implements AddresseeProducer {

    public ImpactedPartiesRecipients(Contact.Role contactRole) {
        super(contactRole);
    }

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        for (Domain domain : td.getImpactedDomains()) {
            addContacts(ret, domain);
        }
        return ret;
    }

}
