package org.iana.rzm.trans.notifications.contact_confirmation;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.DataProducer;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.DomainChangePrinter;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.notifications.default_producer.DefaultTransactionDataProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ContactConfirmationTCDataProducer extends DefaultTransactionDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        PAddressee addressee = (PAddressee) dataSource.get("addressee");
        Map<PAddressee, ContactIdentity> identities = (Map<PAddressee, ContactIdentity>) dataSource.get("identities");
        ContactIdentity contactIdentity = identities != null ? identities.get(addressee) : null;

        if (contactIdentity != null) {
            values.put("roleName", "TC");
            values.put("contactType", "technical");
            values.put("transactionId", "" + dataSource.get("transactionId"));
            values.put("stateName", "" + dataSource.get("stateName"));
            values.put("token", contactIdentity.getToken());
            values.put("domainName", td.getCurrentDomain().getFqdnName());
            values.put("name", contactIdentity.getName());
            values.put("title", getContactJobTitle(td));
            values.put("changes", DomainChangePrinter.print(td.getDomainChange()));
            values.put("currentOrNewContact", contactIdentity.isNewContact() ? "proposed new technical contact" : "current technical contact");
            values.put("newContactOnly", contactIdentity.isNewContact() ? newContactInfo(td) : "");
            values.put("url", "https://rzm.iana.org:8080/rzm");
            values.put("ticket", "" + td.getTicketID());
            values.put("subbmiter", td.getSubmitterEmail() == null ? td.getTrackData().getCreatedBy() : td.getSubmitterEmail());
        }
        return values;
    }

    private String newContactInfo(TransactionData td) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\nYou must also confirm that your proposed address, telephone, and fax\n");
        buffer.append("number and e-mail address are correct and that you agree\n");
        buffer.append("to assume the responsibility as technical\n");
        buffer.append("contact and trustee for the ");
        buffer.append(td.getCurrentDomain().getName());
        buffer.append(" top-level domain as described\n");
        buffer.append("in ICP-1 at <http://www.iana.org/icp/icp-1.htm>.\n");
        return buffer.toString();
    }

    private Contact findContact(TransactionData td) {
        Domain domain = td.getCurrentDomain();
        Map<String, Change> changes = td.getDomainChange().getFieldChanges();
        Contact retContact = domain.getTechContact();

        if (retContact == null) {
            retContact = new Contact();
            ObjectChange newContact = (ObjectChange) changes.get("techContact");

            if ((newContact != null) && (newContact.getFieldChanges().get("jobTitle") != null)) {
                SimpleChange simpleChange = (SimpleChange) newContact.getFieldChanges().get("jobTitle");
                retContact.setJobTitle(simpleChange.getNewValue());
            }
        }
        return retContact;
    }

    private String getContactJobTitle(TransactionData td) {
        Contact currentFullContact = findContact(td);
        if ((currentFullContact == null) || (currentFullContact.getJobTitle() == null) || (currentFullContact.getJobTitle().equals("")))
            return "";
        return " (" + currentFullContact.getJobTitle() + ")";
    }
}
