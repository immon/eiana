package org.iana.rzm.trans.notifications.impacted_parties;

import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.user.SystemRole;
import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesAddresseeProducer extends AbstractTransactionAddresseeProducer {

    protected Set<Addressee> produceAddressee(Map dataSource) {
        Set<Addressee> addressees = new HashSet<Addressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        for (Identity identity : td.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) identity;
            if (cid.isSharedEffect()) {
                addressees.add(new EmailAddressee(cid.getEmail(), cid.getName()));
            }
        }
        return addressees;
    }

}
