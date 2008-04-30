package org.iana.rzm.trans.notifications.default_producer;

import org.iana.notifications.PAddressee;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.producer.AbstractTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class DefaultTransactionAddresseeProducer extends AbstractTransactionAddresseeProducer {

    static Logger logger = Logger.getLogger(DefaultTransactionAddresseeProducer.class);

    private boolean sendToContacts = false;

    private boolean sendToNewContacts = false;

    private boolean sendToAdmins = false;

    private boolean sendToImpactedParties = false;

    public void setSendToContacts(boolean sendToContacts) {
        this.sendToContacts = sendToContacts;
    }

    public void setSendToAdmins(boolean sendToAddmins) {
        this.sendToAdmins = sendToAddmins;
    }

    public void setSendToImpactedParties(boolean sendToImpactedParties) {
        this.sendToImpactedParties = sendToImpactedParties;
    }


    public void setSendToNewContacts(boolean sendToNewContacts) {
        this.sendToNewContacts = sendToNewContacts;
    }

    public Set<PAddressee> produceAddressee(Map dataSource) {
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        Set<PAddressee> addressees = new HashSet<PAddressee>();

        if (sendToContacts) {
            Domain currentDomain = td.getCurrentDomain();
            addContacts(addressees, currentDomain);
        }

        if (sendToNewContacts) {
            addAddressee(addressees, td.getSponsoringOrgChangedEmail());
            addAddressee(addressees, td.getAdminChangedEmail());
            addAddressee(addressees, td.getTechChangedEmail());
        }

        if (sendToAdmins) {
            //addressees.addAll(getAddressees(AdminRole.AdminType.GOV_OVERSIGHT));
            addressees.addAll(getAddressees(AdminRole.AdminType.IANA));
            //addressees.addAll(getAddressees(AdminRole.AdminType.ZONE_PUBLISHER));
        }

        if (sendToImpactedParties) {
            for (Domain domain : td.getImpactedDomains()) {
                addContacts(addressees, domain);
            }
        }
        return addressees;
    }

    public void addAddressee(Set<PAddressee> addressees, String email) {
        if (email != null) {
            addressees.add(new PAddressee(email, email));
        }
    }

    public void addContacts(Set<PAddressee> addressees, Domain domain) {
        addContact(addressees, domain.getAdminContact());
        addContact(addressees, domain.getTechContact());
    }

    public void addContact(Set<PAddressee> addressees, Contact contact) {
        if (contact != null) {
            if (contact.getEmail() != null) {
                addressees.add(new PAddressee(contact.getName(), contact.getEmail()));
            } else {
                logger.warn("null email for contact " + contact.getName() + " " + contact.getDomainRole());
            }
        } else {
            logger.warn("null contact");
        }
    }

}
