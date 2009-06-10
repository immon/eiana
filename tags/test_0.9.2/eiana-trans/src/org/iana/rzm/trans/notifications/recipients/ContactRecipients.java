package org.iana.rzm.trans.notifications.recipients;

import org.apache.log4j.Logger;
import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactRecipients implements AddresseeProducer {

    private static Logger logger = Logger.getLogger(ContactRecipients.class);

    private Contact.Role contactRole;

    public ContactRecipients(Contact.Role contactRole) {
        CheckTool.checkNull(contactRole, "contact role");
        this.contactRole = contactRole;
    }

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> ret = new HashSet<PAddressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        Domain currentDomain = td.getCurrentDomain();
        addContacts(ret, currentDomain);
        return ret;
    }

    protected void addContacts(Set<PAddressee> ret, Domain domain) {
        for (Contact contact : domain.getContacts(contactRole)) {
            addContact(ret, contact);
        }
    }

    protected void addContact(Set<PAddressee> addressees, Contact contact) {
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
