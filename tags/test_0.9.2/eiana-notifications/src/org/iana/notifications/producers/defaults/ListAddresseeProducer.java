package org.iana.notifications.producers.defaults;

import org.iana.notifications.producers.AddresseeProducer;
import org.iana.notifications.PAddressee;
import org.iana.rzm.common.validators.CheckTool;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ListAddresseeProducer implements AddresseeProducer {

    List<String> emails;

    public ListAddresseeProducer(List<String> emails) {
        CheckTool.checkNull(emails, "emails");
        this.emails = emails;
    }

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        for (String email : emails) {
            ret.add(new PAddressee(email, email));
        }
        return ret;
    }
}
