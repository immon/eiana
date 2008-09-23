package org.iana.rzm.trans.notifications.contact_confirmation;

import org.iana.notifications.*;
import org.iana.notifications.producers.*;
import org.iana.objectdiff.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.domain.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.change.*;
import org.iana.rzm.trans.confirmation.contact.*;
import org.iana.rzm.trans.notifications.default_producer.*;
import org.iana.rzm.user.*;

import java.util.*;

/**
 * @author Piotr Tkaczyk
 */
public class ContactConfirmationACDataProducer extends DefaultTransactionDataProducer implements DataProducer {

    private UserManager userManager;

    public ContactConfirmationACDataProducer(UserManager userManager) {
        CheckTool.checkNull(userManager, "user manager is empty");
        this.userManager = userManager;
    }

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        PAddressee addressee = (PAddressee) dataSource.get("addressee");
        Map<PAddressee, ContactIdentity> identities = (Map<PAddressee, ContactIdentity>) dataSource.get("identities");
        ContactIdentity contactIdentity = identities != null ? identities.get(addressee) : null;

        if (contactIdentity != null) {
            values.put("roleName", "AC");
            values.put("contactType", "administrative");
            values.put("transactionId", "" + dataSource.get("transactionId"));
            values.put("stateName", "" + dataSource.get("stateName"));
            values.put("token", contactIdentity.getToken());
            values.put("domainName", td.getCurrentDomain().getFqdnName());
            values.put("name", contactIdentity.getName());
            values.put("title", getContactJobTitle(td));
            values.put("changes", DomainChangePrinter.print(td.getDomainChange()));
            values.put("currentOrNewContact",
                       contactIdentity.isNewContact() ?
                       "proposed new administrative contact" :
                       "current administrative contact");
            values.put("newContactOnly", contactIdentity.isNewContact() ? newContactInfo(td) : "");
            values.put("url", "https://rzm.iana.org/rzm");
            values.put("ticket", "" + td.getTicketID());
            values.put("period", "" + dataSource.get("period"));


            RZMUser logInUser = userManager.get(td.getTrackData().getCreatedBy());
            values.put("subbmiter",
                       logInUser == null ?
                       (td.getSubmitterEmail() != null ? td.getSubmitterEmail() : "") :
                       logInUser.getName());
        }
        return values;
    }

    private String newContactInfo(TransactionData td) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\nYou must also confirm that your proposed address, telephone, and fax\n");
        buffer.append("number and e-mail address are correct and that you agree\n");
        buffer.append("to assume the responsibility as administrative\n");
        buffer.append("contact and trustee for the ");
        buffer.append(td.getCurrentDomain().getName());
        buffer.append(" top-level domain as described\n");
        buffer.append("in ICP-1 at <http://www.iana.org/icp/icp-1.htm>.\n");
        return buffer.toString();
    }

    private boolean isNewContact(TransactionData td) {
        Map<String, Change> changes = td.getDomainChange().getFieldChanges();
        Change change = changes.get("adminContact");
        if ((change != null) && (change.isAddition())) {
            return true;
        }
        return false;
    }

    private Contact findContact(TransactionData td) {
        Domain domain = td.getCurrentDomain();
        Map<String, Change> changes = td.getDomainChange().getFieldChanges();
        Contact retContact = domain.getAdminContact();

        if (retContact == null) {
            retContact = new Contact();
            ObjectChange newContact = (ObjectChange) changes.get("adminContact");
            if ((newContact != null) && (newContact.getFieldChanges().get("jobTitle") != null)) {
                SimpleChange simpleChange = (SimpleChange) newContact.getFieldChanges().get("jobTitle");
                retContact.setJobTitle(simpleChange.getNewValue());
            }
        }
        return retContact;
    }

    private String getContactJobTitle(TransactionData td) {
        Contact currentFullContact = findContact(td);
        if ((currentFullContact == null) || (currentFullContact.getJobTitle() == null)) {
            return "";
        }
        return " (" + currentFullContact.getJobTitle() + ")";
    }
}
