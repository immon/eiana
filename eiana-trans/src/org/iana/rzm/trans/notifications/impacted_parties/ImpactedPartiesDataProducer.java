package org.iana.rzm.trans.notifications.impacted_parties;

import org.iana.notifications.producers.DataProducer;
import org.iana.notifications.PAddressee;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.DomainChangePrinter;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.Identity;
import org.iana.rzm.trans.notifications.default_producer.DefaultTransactionDataProducer;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.Role;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesDataProducer extends DefaultTransactionDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        ContactIdentity contactIdentity = null;
        PAddressee addressee = (PAddressee) dataSource.get("addressee");

        for (Identity identity : td.getContactConfirmations().getUsersAbleToAccept()) {
            ContactIdentity cid = (ContactIdentity) identity;
            Role.Type type = cid.getType();
            String email = cid.getEmail();
            if (type == contactIdentity.getType() &&
                    contactIdentity.getName() != null && contactIdentity.getName().equals(addressee.getName()) &&
                    email != null && email.equals(addressee.getEmail())) {
                contactIdentity = (ContactIdentity) identity;
                break;
            }
        }

        if (contactIdentity != null) {
            values.put("roleName", ""+contactIdentity.getType());
            values.put("contactType", contactIdentity.getType() == SystemRole.SystemType.TC ? "technical" : "administrative");
            values.put("transactionId", "" + dataSource.get("transactionId"));
            values.put("stateName", "" + dataSource.get("stateName"));
            values.put("token", contactIdentity.getToken());
            values.put("domainName", contactIdentity.getDomainName());
            values.put("name", contactIdentity.getName());
            // values.put("title", getContactJobTitle(td));
            values.put("changes", DomainChangePrinter.print(td.getDomainChange()));
            // values.put("currentOrNewContact", isNewContact(td) ? "proposed new technical contact" : "current technical contact");
            // values.put("newContactOnly", isNewContact(td) ? newContactInfo(td) : "");
            values.put("url", "https://rzm.iana.org:8080/rzm");
            values.put("ticket", "" + td.getTicketID());
            values.put("subbmiter", td.getSubmitterEmail() == null ? td.getTrackData().getCreatedBy() : td.getSubmitterEmail());
        }
        return values;
    }
    
}
