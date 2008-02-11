package org.iana.rzm.trans.notifications.contact_confirmation;

import org.iana.notifications.PAddressee;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ContactConfirmationACAddresseeProducer extends AbstractTransactionAddresseeProducer {

    public Set<PAddressee> produceAddressee(Map dataSource) {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        if (td != null) {
            Domain currentDomain = td.getCurrentDomain();
            if (currentDomain != null) {
                Contact adminContact = currentDomain.getAdminContact();
                if (adminContact != null) {
                    addressees.add(new PAddressee(adminContact.getName(), adminContact.getEmail()));
                }
                String email = td.getAdminChangedEmail();
                if (email != null) {
                    addressees.add(new PAddressee(email, email));
                }
            }
        }
        return addressees;
    }
}
