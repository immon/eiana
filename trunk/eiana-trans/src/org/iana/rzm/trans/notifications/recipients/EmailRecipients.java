package org.iana.rzm.trans.notifications.recipients;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class EmailRecipients implements AddresseeProducer {

    Set<String> emails;

    public EmailRecipients(List<String> emails) {
        this.emails = new HashSet<String>(emails);
    }

    public EmailRecipients(Set<String> emails) {
        this.emails = emails;
    }

    public EmailRecipients(String email) {
        this.emails = new HashSet<String>();
        this.emails.add(email);
    }

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        String email = (String) dataSource.get("email");
        if (CheckTool.isCorrectEmali(email)) {
            ret.add(new PAddressee(email, email));
        }

        for (String e : emails) {
            if (CheckTool.isCorrectEmali(e)) {
                ret.add(new PAddressee(e, e));
            }
        }

        return ret;
    }

}
