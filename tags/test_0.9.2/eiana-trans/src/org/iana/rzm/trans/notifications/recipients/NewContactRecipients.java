package org.iana.rzm.trans.notifications.recipients;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.trans.TransactionData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class NewContactRecipients implements AddresseeProducer {

    private Contact.Role contactRole;

    public NewContactRecipients(Contact.Role contactRole) {
        CheckTool.checkNull(contactRole, "contact role");
        this.contactRole = contactRole;
    }

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        switch (contactRole) {
            case AC:
                addAddressee(ret, td.getAdminChangedEmail());
                break;
            case TC:
                addAddressee(ret, td.getTechChangedEmail());
                break;
            case SO:
                addAddressee(ret, td.getSponsoringOrgChangedEmail());
                break;
            default:
        }
        return ret;
    }

    public void addAddressee(Set<PAddressee> addressees, String email) {
        if (email != null) {
            addressees.add(new PAddressee(email, email));
        }
    }

}
