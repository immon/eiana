package org.iana.rzm.trans.notifications.impacted_parties;

import org.iana.rzm.trans.notifications.default_producer.DefaultDataProducer;
import org.iana.rzm.trans.notifications.producer.DataProducer;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.change.DomainChangePrinter;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesDataProducer extends DefaultDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");

        ContactIdentity contactIdentity = (ContactIdentity) dataSource.get("addressee");

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
