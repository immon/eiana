package org.iana.rzm.trans.notifications.impacted_parties;

import org.iana.notifications.*;
import org.iana.notifications.producers.*;
import org.iana.rzm.trans.*;
import org.iana.rzm.trans.change.*;
import org.iana.rzm.trans.confirmation.contact.*;
import org.iana.rzm.trans.notifications.default_producer.*;
import org.iana.rzm.user.*;

import java.util.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesDataProducer extends DefaultTransactionDataProducer implements DataProducer {

    public Map<String, String> getSpecificValuesMap(Map dataSource) {
        Map<String, String> values = new HashMap<String, String>();
        Map<PAddressee, ContactIdentity> identities = (Map<PAddressee, ContactIdentity>) dataSource.get("identities");
        PAddressee addressee = (PAddressee) dataSource.get("addressee");
        ContactIdentity contactIdentity = identities != null ? identities.get(addressee) : null;
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        if (contactIdentity != null) {
            values.put("roleName", ""+contactIdentity.getType());
            values.put("contactType", contactIdentity.getType() == SystemRole.SystemType.TC ? "technical" : "administrative");
            values.put("transactionId", "" + dataSource.get("transactionId"));
            values.put("stateName", "" + dataSource.get("stateName"));
            values.put("token", contactIdentity.getToken());
            values.put("domainName", "." + contactIdentity.getDomainName().toUpperCase());
            values.put("name", contactIdentity.getName());
            //values.put("title", getContactJobTitle(td));
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
