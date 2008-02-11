package org.iana.rzm.trans.notifications.impacted_parties;

import org.iana.notifications.PAddressee;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.Identity;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesAddresseeProducer extends AbstractTransactionAddresseeProducer {

    protected Set<PAddressee> produceAddressee(Map dataSource) {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        for (Identity identity : td.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) identity;
            if (cid.isSharedEffect()) {
                addressees.add(new PAddressee(cid.getName(), cid.getEmail()));
            }
        }
        return addressees;
    }

}
