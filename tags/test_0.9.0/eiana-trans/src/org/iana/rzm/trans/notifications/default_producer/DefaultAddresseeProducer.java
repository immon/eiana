package org.iana.rzm.trans.notifications.default_producer;

import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class DefaultAddresseeProducer extends AbstractTransactionAddresseeProducer {

    private boolean sendToContacts;
    private boolean sendToAdmins;


    public void setSendToContacts(boolean sendToContacts) {
        this.sendToContacts = sendToContacts;
    }

    public void setSendToAdmins(boolean sendToAddmins) {
        this.sendToAdmins = sendToAddmins;
    }

    public Set<Addressee> produceAddressee(Map dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        Domain currentDomain = td.getCurrentDomain();
        Set<Addressee> addressees = new HashSet<Addressee>();

        // bug: selection: all users in any role for a given domain name!
        if (sendToContacts) {
            Contact adminContact = currentDomain.getAdminContact();
            if (adminContact != null)
                addressees.add(new EmailAddressee(adminContact.getEmail(), adminContact.getName()));

            Contact techContact = currentDomain.getTechContact();
            if (techContact != null)
                addressees.add(new EmailAddressee(techContact.getEmail(), techContact.getName()));

            if (td.isRedelegation()) {
                Contact supportingContact = currentDomain.getSupportingOrg();
                if (supportingContact != null)
                    addressees.add(new EmailAddressee(supportingContact.getEmail(), supportingContact.getName()));
            }
        }

        if (sendToAdmins) {
            addressees.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)).getUsersAbleToAccept());
            addressees.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.IANA)).getUsersAbleToAccept());
            addressees.addAll(new RoleConfirmation(new AdminRole(AdminRole.AdminType.ZONE_PUBLISHER)).getUsersAbleToAccept());
        }

        return addressees;
    }
}
