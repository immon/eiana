package org.iana.rzm.trans.notifications.recipients;

import org.apache.log4j.Logger;
import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.SystemRole;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactConfirmationRecipients implements AddresseeProducer {

    private static Logger logger = Logger.getLogger(ContactRecipients.class);

    private SystemRole.SystemType systemType;

    public ContactConfirmationRecipients(SystemRole.SystemType systemType) {
        CheckTool.checkNull(systemType, "system role.type");
        this.systemType = systemType;
    }

    public Set<PAddressee> produce(Map dataSource) {
        Map<PAddressee, ContactIdentity> identities = new HashMap<PAddressee, ContactIdentity>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        ContactConfirmations confirmations = td.getContactConfirmations(TransactionState.Name.PENDING_CONTACT_CONFIRMATION);
        for (ContactIdentity cid : confirmations.getIdentitiesSupposedToAccept()) {
            if (cid.getType() == systemType) {
                identities.put(
                        new PAddressee(cid.getName(), cid.getEmail()),
                        cid
                );
            }
        }
        Map<PAddressee, ContactIdentity> existingIdentities = (Map<PAddressee, ContactIdentity>) dataSource.get("identities");
        if (existingIdentities == null) {
            dataSource.put("identities", identities);
        } else {
            existingIdentities.putAll(identities);
        }
        return identities.keySet();
    }

}
