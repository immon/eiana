package org.iana.rzm.trans.notifications.recipients;

import org.iana.notifications.PAddressee;
import org.iana.notifications.producers.AddresseeProducer;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.TransactionData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SubmitterRecipients implements AddresseeProducer {

    public Set<PAddressee> produce(Map dataSource) {
        Set<PAddressee> addressees = new HashSet<PAddressee>();
        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        String submitterEmail = td.getSubmitterEmail();
        if (CheckTool.isCorrectEmali(submitterEmail)) {
            addressees.add(new PAddressee(submitterEmail, submitterEmail));
        }
        return addressees;
    }
    
}
