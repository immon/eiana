package org.iana.rzm.trans.notifications.contact_confirmation;

import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;
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
public class ContactConfirmationTCAddresseeProducer extends AbstractTransactionAddresseeProducer {

    public Set<Addressee> produceAddressee(Map dataSource) {
        Set<Addressee> addressees = new HashSet<Addressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        if (td != null) {
            Domain currentDomain = td.getCurrentDomain();
            if (currentDomain != null) {
                Contact techContact = currentDomain.getTechContact();
                if (techContact != null)
                    addressees.add(new EmailAddressee(techContact.getEmail(), techContact.getName()));
            }
        }
        return addressees;
    }
}
