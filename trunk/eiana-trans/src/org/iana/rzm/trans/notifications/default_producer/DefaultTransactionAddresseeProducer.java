package org.iana.rzm.trans.notifications.default_producer;

import org.iana.notifications.refactored.PAddressee;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class DefaultTransactionAddresseeProducer extends AbstractTransactionAddresseeProducer {

    private boolean sendToContacts;
    private boolean sendToAdmins;


    public void setSendToContacts(boolean sendToContacts) {
        this.sendToContacts = sendToContacts;
    }

    public void setSendToAdmins(boolean sendToAddmins) {
        this.sendToAdmins = sendToAddmins;
    }

    public Set<PAddressee> produceAddressee(Map dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        Domain currentDomain = td.getCurrentDomain();
        Set<PAddressee> addressees = new HashSet<PAddressee>();

        // bug: selection: all users in any role for a given domain name!
        if (sendToContacts) {
            Contact adminContact = currentDomain.getAdminContact();
            if (adminContact != null)
                addressees.add(new PAddressee(adminContact.getName(), adminContact.getEmail()));

            Contact techContact = currentDomain.getTechContact();
            if (techContact != null)
                addressees.add(new PAddressee(techContact.getName(), techContact.getEmail()));

            if (td.isRedelegation()) {
                Contact supportingContact = currentDomain.getSupportingOrg();
                if (supportingContact != null)
                    addressees.add(new PAddressee(supportingContact.getName(), supportingContact.getEmail()));
            }
        }

        if (sendToAdmins) {
            addressees.addAll(getAddressees(AdminRole.AdminType.GOV_OVERSIGHT));
            addressees.addAll(getAddressees(AdminRole.AdminType.IANA));
            addressees.addAll(getAddressees(AdminRole.AdminType.ZONE_PUBLISHER));
        }

        return addressees;
    }

}
