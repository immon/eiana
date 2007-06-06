package org.iana.rzm.trans.collectors;

import org.iana.objectdiff.Change;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import static org.iana.rzm.user.SystemRole.SystemType.AC;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class ContactNotificationDataCollector extends AbstractNotificationDataCollector {

    private TransactionData td;
    private ContactIdentity contact;
    private Long transactionId;
    private String stateName;
    Contact currentFullContact;

    public ContactNotificationDataCollector(TransactionData td, ContactIdentity contact, Long transactionId, String stateName) {
        this.td = td;
        this.contact = contact;
        this.transactionId = transactionId;
        this.stateName = stateName;
        currentFullContact = findContact();
    }

    public Map<String, String> getValuesMap() {
        Map<String, String> values = new HashMap<String, String>();

        if ((td != null) && (contact != null)) {

            values.put("roleName", contact.getType().toString());
            values.put("transactionId", "" + transactionId);
            values.put("stateName", stateName);
            values.put("token", contact.getToken());

            values.put("domainName", td.getCurrentDomain().getName());
            values.put("name", contact.getName());
            values.put("title", getContactJobTitle());

            values.put("changes", getChanges(td));

            values.put("currentOrNewContact", (isNewContact()) ? "proposed new contact" : "current contact");
            values.put("newContactOnly", (isNewContact()) ? newContactInfo() : "");
            values.put("url", "http://rzm.iana.org/test");
            values.put("ticket", "12345");
        }
        return values;
    }

    private String newContactInfo() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\nYou must also confirm that your proposed address, telephone, and fax\n");
        buffer.append("number and e-mail address are correct and that you agree\n");
        buffer.append("to assume the responsibility as ");
        buffer.append((contact.getType() == AC) ? "administrative\n" : "technical\n");
        buffer.append("contact and trustee for the ");
        buffer.append(td.getCurrentDomain().getName());
        buffer.append(" top-level domain as described\n");
        buffer.append("in ICP-1 at <http://www.iana.org/icp/icp-1.htm>.\n");
        return buffer.toString();
    }

    private boolean isNewContact() {
        Map<String, Change> changes = td.getDomainChange().getFieldChanges();
        Change change;
        if ((isAC()) && (changes.containsKey("adminContact")))
            change = changes.get("adminContact");
        else
            change = changes.get("techContact");

        if ((change != null) && (change.isAddition())) return true;

        return false;
    }

    public boolean isAC() {
        return (contact.getType() == AC);
    }

    private Contact findContact() {
        Domain domain = td.getCurrentDomain();
        Map<String, Change> changes = td.getDomainChange().getFieldChanges();
        Contact retContact = domain.getAdminContact();
        if ((retContact != null) && (!retContact.getName().equals(contact.getName())))
            retContact = domain.getTechContact();

        if (retContact == null) {
            ObjectChange newContact;
            retContact = new Contact();
            if (isAC())
                newContact = (ObjectChange) changes.get("adminContact");
            else
                newContact = (ObjectChange) changes.get("techContact");

            if ((newContact != null) && (newContact.getFieldChanges().get("jobTitle") != null)) {
                SimpleChange simpleChange = (SimpleChange) newContact.getFieldChanges().get("jobTitle");
                retContact.setJobTitle(simpleChange.getNewValue());
            }
        }
        return retContact;
    }

    private String getContactJobTitle() {
        if ((currentFullContact == null) || (currentFullContact.getJobTitle() == null) || (currentFullContact.getJobTitle().equals("")))
            return "";
        return " (" + currentFullContact.getJobTitle() + ")";
    }
}
