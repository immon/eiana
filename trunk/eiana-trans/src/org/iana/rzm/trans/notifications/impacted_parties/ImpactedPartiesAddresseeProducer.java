package org.iana.rzm.trans.notifications.impacted_parties;

import org.iana.notifications.PAddressee;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.TransactionState;
import org.iana.rzm.trans.confirmation.Identity;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesAddresseeProducer extends AbstractTransactionAddresseeProducer {

    protected Set<PAddressee> produceAddressee(Map dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        Map<PAddressee, ContactIdentity> identities = new HashMap<PAddressee, ContactIdentity>();
        ContactConfirmations conf = td.getContactConfirmations(TransactionState.Name.PENDING_IMPACTED_PARTIES);
        for (Identity identity : conf.getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) identity;
            if (cid.isSharedEffect()) {
                identities.put(new PAddressee(cid.getName(), cid.getEmail()), cid);
            }
        }
        dataSource.put("identities", identities);
        return identities.keySet();
    }

}
