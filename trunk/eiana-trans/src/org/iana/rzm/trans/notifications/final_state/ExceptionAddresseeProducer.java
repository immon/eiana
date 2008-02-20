package org.iana.rzm.trans.notifications.final_state;

import org.iana.notifications.PAddressee;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.notifications.default_producer.DefaultTransactionAddresseeProducer;
import org.iana.rzm.user.AdminRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class ExceptionAddresseeProducer extends DefaultTransactionAddresseeProducer {
    List<String> additionalEmails = new ArrayList<String>();

    public void setAdditionalEmails(List<String> additionalEmails) {
        this.additionalEmails = additionalEmails;
    }

    public Set<PAddressee> produceAddressee(Map dataSource) {
        Set<PAddressee> retAddressees = super.produceAddressee(dataSource);
        for (String email : additionalEmails)
            retAddressees.add(new PAddressee(email, email));

        retAddressees.addAll(getAddressees(AdminRole.AdminType.IANA));

        TransactionData td = (TransactionData) dataSource.get("TRANSACTION_DATA");
        String submitter = td.getSubmitterEmail();
        retAddressees.add(new PAddressee(submitter, submitter));

        return retAddressees;
    }
}
