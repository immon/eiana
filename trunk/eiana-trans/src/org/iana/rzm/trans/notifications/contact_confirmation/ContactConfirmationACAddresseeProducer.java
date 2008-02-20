package org.iana.rzm.trans.notifications.contact_confirmation;

import org.iana.notifications.PAddressee;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.SystemRole;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ContactConfirmationACAddresseeProducer extends AbstractTransactionAddresseeProducer {

    public Set<PAddressee> produceAddressee(Map dataSource) {
        Map<PAddressee, ContactIdentity> identities = new HashMap<PAddressee, ContactIdentity>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        ContactConfirmations confirmations = td.getContactConfirmations(TransactionState.Name.PENDING_CONTACT_CONFIRMATION);
        for (ContactIdentity cid : confirmations.getIdentitiesSupposedToAccept()) {
            if (cid.getType() == SystemRole.SystemType.AC) {
                identities.put(
                        new PAddressee(cid.getName(), cid.getEmail()),
                        cid
                );
            }
        }
        dataSource.put("identities", identities);
        return identities.keySet();
    }
}
