package org.iana.rzm.trans.notifications.recipients;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmailRecipients implements AddresseeProducer {

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        String email = (String) dataSource.get("email");
        if (CheckTool.isCorrectEmali(email)) {
            ret.add(new PAddressee(email, email));
        }
        return ret;
    }

}
